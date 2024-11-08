import pandas as pd

# Function to read the header of a CSV and find columns containing specific substrings
def find_columns_with_substrings(file_path, substrings):
    # Read only the header of the CSV file
    df = pd.read_csv(file_path, nrows=0)
    columns = df.columns

    # Find columns containing any of the specified substrings
    matching_columns = [col for col in columns if any(sub in col for sub in substrings)]

    return matching_columns

# Specify the file path to your CSV
file_path = 'your_file.csv'  # Replace with your actual file path

# List of substrings to search for
substrings = ['DT', 'CD', 'KY']

# Find and print matching columns
matching_columns = find_columns_with_substrings(file_path, substrings)
print("Columns containing DT, CD, or KY:", matching_columns)
