import subprocess

# Define the curl command as a list of arguments
curl_command = [
    'curl',
    '-X', 'POST',
    'https://xyz.com/api/temp.docx',
    '-H', 'Ins: {"type": "file"}',
    '-H', 'Authorization: Bearer xyz',
    '-H', 'Content-type: application/json',
    '--data-binary', 'temp.docx'
]

try:
    # Execute the curl command
    result = subprocess.run(curl_command, capture_output=True, text=True, check=True)

    # Print the command's standard output
    print("Command output:")
    print(result.stdout)

except subprocess.CalledProcessError as e:
    # Handle any errors
    print(f"Command failed with error code {e.returncode}")
    print("Error output:")
    print(e.stderr)
