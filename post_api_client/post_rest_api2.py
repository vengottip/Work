import requests
import urllib3
import json

# Disable SSL certificate verification
urllib3.disable_warnings()

# API endpoint URL
api_url = "https://example.com/api/endpoint"

# Authorization code
authorization_code = "xxxxxxx"

# Create the headers with the authorization token
headers = {
    "Authorization": f"Bearer {authorization_code}",
    "Instatbase-API-Args": json.dumps({
        "type": "file",
        "cursor": 0,
        "if_exists": "overwrite",
        "mime_type": "application/pdf"
    })
}

# Send the POST request with SSL certificate verification disabled
try:
    response = requests.post(api_url, headers=headers, verify=False)

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
