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
            source_files = [f for f in os.listdir(source_dir) if f.startswith(file_name)]
            
            if source_files:
                for source_file in source_files:
                    file_path = os.path.join(source_dir, source_file)
                    destination_path = os.path.join(destination_dir, source_file)
                    shutil.move(file_path, destination_path)
                    print(f"Moved file '{source_file}' to '{destination_dir}'")
            else:
                print(f"No files found in '{source_dir}' starting with '{file_name}'")

# Usage example
csv_file = 'path/to/csv/file.csv'
source_dir = 'path/to/source/directory'
destination_dir = 'path/to/destination/directory'

move_files(csv_file, source_dir, destination_dir)