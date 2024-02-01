import os
import csv
import shutil

def move_files(csv_file, source_dir, destination_dir):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        next(reader)  # Skip header row
        for row in reader:
            file_and_record_id = row[0]
            file_name = file_and_record_id.split('.')[0]
            file_path = os.path.join(source_dir, file_name)
            if os.path.isfile(file_path):
                destination_path = os.path.join(destination_dir, file_name)
                shutil.move(file_path, destination_path)
                print(f"Moved file '{file_name}' to '{destination_dir}'")
            else:
                print(f"File '{file_name}' not found in '{source_dir}'")

# Usage example
csv_file = 'path/to/csv/file.csv'
source_dir = 'path/to/source/directory'
destination_dir = 'path/to/destination/directory'

move_files(csv_file, source_dir, destination_dir)
