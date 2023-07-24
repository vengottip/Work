import spacy
from nltk import sent_tokenize

def extract_content(text):
    # Load the SpaCy English model
    nlp = spacy.load('en_core_web_sm')
    
    # Tokenize the text into sentences using NLTK
    sentences = sent_tokenize(text)
    
    # Process each sentence with SpaCy
    for sentence in sentences:
        doc = nlp(sentence)
        
        # Extract relevant content using SpaCy's POS tagging and dependency parsing
        content = []
        for token in doc:
            if token.pos_ in ['NOUN', 'PROPN', 'VERB', 'ADJ']:
                content.append(token.text)
        
        # Print the extracted content for each sentence
        print(' '.join(content))

if __name__ == "__main__":
    input_text = """Your input text goes here. It can contain multiple sentences. 
                    The program will extract nouns, proper nouns, verbs, and adjectives from each sentence."""
    
    extract_content(input_text)
