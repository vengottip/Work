from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import CharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_chroma import Chroma

# ❌ Removed Ollama import
# from langchain_community.llms import Ollama

from langchain.prompts import PromptTemplate
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.chains import create_retrieval_chain

import os

# ✅ ADDED: Hugging Face Transformers
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
import torch

# Set offline environment variables
os.environ["TRANSFORMERS_OFFLINE"] = "1"
os.environ["HF_DATASETS_OFFLINE"] = "1"
os.environ["TRANSFORMERS_CACHE"] = "./cache"

# ✅ Custom wrapper class for Hugging Face LLM (replaces Ollama)
from langchain_core.language_models import LLM

class HuggingFaceLLM(LLM):
    def __init__(self, model_path: str):
        self.tokenizer = AutoTokenizer.from_pretrained(model_path)
        self.model = AutoModelForCausalLM.from_pretrained(model_path, torch_dtype=torch.float16).to("cuda" if torch.cuda.is_available() else "cpu")
        self.pipe = pipeline("text-generation", model=self.model, tokenizer=self.tokenizer, device=0 if torch.cuda.is_available() else -1)

    def _call(self, prompt: str, stop=None) -> str:
        output = self.pipe(prompt, max_new_tokens=256, do_sample=True)
        return output[0]["generated_text"]

    @property
    def _identifying_params(self):
        return {"model": "gemma-2b"}

    @property
    def _llm_type(self):
        return "custom_hf_llm"

# ✅ Instantiate Hugging Face model (assumes you've copied it locally)
llm = HuggingFaceLLM(model_path="./gemma-2b")  # <--- folder with model files (downloaded manually)

# ✅ Load your text document
textloader = TextLoader("./documents/llama2paper.md", encoding="utf-8")
documents = textloader.load()

text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=100, separator="\n\n")
docs = text_splitter.split_documents(documents)

embedding_function = HuggingFaceEmbeddings(model_name="./all-MiniLM-L6-v2")

db = Chroma.from_documents(docs, embedding_function)
retriever = db.as_retriever(search_kwargs={"k": 1})

# ✅ Use prompt template
retrieval_qa_chat_prompt = PromptTemplate.from_template(
    "Use the following context to answer the question.\n\n"
    "Context:\n{context}\n\n"
    "Question:\n{input}\n\n"
    "Answer:"
)

combine_docs_chain = create_stuff_documents_chain(llm, retrieval_qa_chat_prompt)
rag_chain = create_retrieval_chain(db.as_retriever(), combine_docs_chain)

# ✅ Run sample queries
res = rag_chain.invoke({"input": "What is the Table of Content for the paper 'Llama 2: Open Foundation and Fine-Tuned Chat Models'?"})
answer = res['answer']

res2 = rag_chain.invoke({"input": "How has Llama 2 improved model convergence speed during training?"})

print(answer)
