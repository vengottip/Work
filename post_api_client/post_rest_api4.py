import subprocess
import os

# Define the number of iterations
num_iterations = 5

# Define the base curl command template with placeholders
curl_command_template = 'curl -X GET https://example.com/api/{endpoint} -H "Authorization: {token}"'

# Define the dynamic_values array
dynamic_values = [
    {"endpoint": "endpoint1", "token": "token1"},
    {"endpoint": "endpoint2", "token": "token2"},
    # Add more values as needed
]

# Folder containing files
folder_path = "/path/to/folder"  # Replace with the actual folder path

# List files in the folder
file_names = os.listdir(folder_path)

# Add file names to dynamic_values array with a key of "fileName"
for file_name in file_names:
    dynamic_values.append({"endpoint": file_name, "token": "your_token", "fileName": file_name})

# Ensure that the dynamic_values list contains enough entries for num_iterations
while len(dynamic_values) < num_iterations:
    dynamic_values.append({"endpoint": "default", "token": "default"})

for iteration in range(num_iterations):
    dynamic_value = dynamic_values[iteration]

    curl_command = curl_command_template.format(**dynamic_value)

    try:
        result = subprocess.run(curl_command, shell=True, capture_output=True, text=True, check=True)

        print(f"Iteration {iteration + 1} - Command output:")
        print(result.stdout)

    except subprocess.CalledProcessError as e:
        print(f"Iteration {iteration + 1} - Command failed with error code {e.returncode}")
        print("Error output:")
        print(e.stderr)
