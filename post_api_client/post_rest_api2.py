import requests

# API endpoint URL
api_url = "https://example.com/api/endpoint"

# Authorization code
authorization_code = "xxxxxxx"

# Path to the Word document
document_path = "c:/abc.docx"

# Data to be sent as binary
binary_data = b"Your binary data here"

# Create the headers with the authorization token
headers = {
    "Authorization": f"Bearer {authorization_code}",
}

# Create a dictionary for the file to be sent
files = {
    "file": ("abc.docx", open(document_path, "rb")),
    "data-binary": ("data.bin", binary_data)
}

# Send the POST request
try:
    response = requests.post(api_url, headers=headers, files=files)

    # Check if the request was successful (status code 200)
    if response.status_code == 200:
        print("POST request was successful")
        print("Response:")
        print(response.json())  # Assuming the response is in JSON format
    else:
        print(f"POST request failed with status code: {response.status_code}")
        print("Response:")
        print(response.text)  # Print the response content in case of an error

except requests.exceptions.RequestException as e:
    print(f"An error occurred: {e}")
