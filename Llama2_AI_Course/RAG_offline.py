from langchain_community.document_loaders import TextLoader
from langchain_text_splitters import CharacterTextSplitter
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_chroma import Chroma

from langchain_community.llms import Ollama
#from langchain import hub # REMOVED FOR OFFLINE ### CHANGED ###
from langchain.prompts import PromptTemplate  # ADDED FOR OFFLINE ###
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain.chains import create_retrieval_chain

import os  # ADDED FOR OFFLINE ###

# Set transformers and datasets to offline mode ### ADDED ###
os.environ["TRANSFORMERS_OFFLINE"] = "1"
os.environ["HF_DATASETS_OFFLINE"] = "1"
# Set the cache directory for Hugging Face models ### ADDED ###
os.environ["TRANSFORMERS_CACHE"] = "./cache"  # ADDED ###
os.environ["OLLAMA_OFFLINE"] = "1"
llm = Ollama(model="gemma:2b")

# load the document and split it into chunks
#textloader = TextLoader("./documents/llama2paper.md")
textloader = TextLoader("./documents/llama2paper.md", encoding="utf-8")
# load the documents
# load the documents from the text file
# Note: The text file should be in the same directory as this script
# or provide the full path to the text file
# load the documents from the text file
# Note: The text file should be in the same directory as this script
# or provide the full path to the text file
# load the documents from the text file

documents = textloader.load()

# split it into chunks
#text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=0)
text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=100, separator="\n\n")
# split the documents into chunks
# Note: The chunk size and overlap can be adjusted based on your requirements
docs = text_splitter.split_documents(documents)

# create the open-source embedding function
embedding_function = HuggingFaceEmbeddings(model_name="./all-MiniLM-L6-v2")

# load it into Chroma
db = Chroma.from_documents(docs, embedding_function)

retriever = db.as_retriever(search_kwargs={"k": 1})

#llm = Ollama(model="AIresearcher:latest")
#llm = Ollama(model="llama2:7b")  # ✅ Use a model you have
#llm = Ollama(model="mistral")
llm = Ollama(model="gemma:2b")
# load the model from Ollama
# Note: You can also use other models available on Ollama
# load the model from Ollama
# Note: You can also use other models available on Ollama


# load the retrieval-qa-chat prompt from the hub
# Note: You can also create your own prompt if you want
# load the retrieval-qa-chat prompt from the hub
# Note: You can also create your own prompt if you want
# load the retrieval-qa-chat prompt from the hub    



# retrieval_qa_chat_prompt = hub.pull("langchain-ai/retrieval-qa-chat")

# Define the prompt locally (instead of pulling from the LangChain hub) ### CHANGED ###
retrieval_qa_chat_prompt = PromptTemplate.from_template(
    "Use the following context to answer the question.\n\n"
    "Context:\n{context}\n\n"
    "Question:\n{input}\n\n"
    "Answer:"
)

combine_docs_chain = create_stuff_documents_chain(llm, retrieval_qa_chat_prompt)
rag_chain = create_retrieval_chain(db.as_retriever(), combine_docs_chain)

res = rag_chain.invoke({"input": "What is the Table of Content for the paper 'Llama 2: Open Foundation and Fine-Tuned Chat Models'?"})
answer = res['answer']

res = rag_chain.invoke({"input": "How has Llama 2 improved model convergence speed during training?"})


print(answer)