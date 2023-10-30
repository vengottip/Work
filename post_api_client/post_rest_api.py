import requests

# Define the API endpoint
api_url = "https://example.com/api/endpoint"

# Define the authorization token
authorization_code = "xxxxxxx"

# Define the file to be sent
file_path = "abc.docx"

# Create the headers with the authorization token
headers = {
    "Authorization": f"Bearer {authorization_code}",
}

# Create a dictionary for the file to be sent
files = {"file": (file_path, open(file_path, "rb"))}

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
