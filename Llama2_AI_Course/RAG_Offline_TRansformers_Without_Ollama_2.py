from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import CharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_chroma import Chroma

from langchain.prompts import PromptTemplate
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.chains import create_retrieval_chain

from langchain_community.llms import HuggingFacePipeline
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
import os

# Offline environment setup
os.environ["TRANSFORMERS_OFFLINE"] = "1"
os.environ["HF_DATASETS_OFFLINE"] = "1"
os.environ["TRANSFORMERS_CACHE"] = "./cache"

# Load and split the document
textloader = TextLoader("./documents/llama2paper.md", encoding="utf-8")
documents = textloader.load()

text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=100, separator="\n\n")
docs = text_splitter.split_documents(documents)

# Embeddings
embedding_function = HuggingFaceEmbeddings(model_name="./all-MiniLM-L6-v2")
db = Chroma.from_documents(docs, embedding_function)
retriever = db.as_retriever(search_kwargs={"k": 1})

# Load a small, local Hugging Face language model
# You can replace this with any local GGUF or downloaded model path
model_name = "./tiny-llm/gpt-neo-125M"  # e.g., "EleutherAI/gpt-neo-125M" downloaded offline

tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(model_name)

pipe = pipeline(
    "text-generation",
    model=model,
    tokenizer=tokenizer,
    max_new_tokens=512,
    do_sample=True,
    temperature=0.7,
)

llm = HuggingFacePipeline(pipeline=pipe)

# Prompt setup
retrieval_qa_chat_prompt = PromptTemplate.from_template(
    "Use the following context to answer the question.\n\n"
    "Context:\n{context}\n\n"
    "Question:\n{input}\n\n"
    "Answer:"
)

combine_docs_chain = create_stuff_documents_chain(llm, retrieval_qa_chat_prompt)
rag_chain = create_retrieval_chain(retriever, combine_docs_chain)

# Run some queries
res = rag_chain.invoke({
    "input": "What is the Table of Content for the paper 'Llama 2: Open Foundation and Fine-Tuned Chat Models'?"
})
print(res['answer'])

res = rag_chain.invoke({
    "input": "How has Llama 2 improved model convergence speed during training?"
})
print(res['answer'])
