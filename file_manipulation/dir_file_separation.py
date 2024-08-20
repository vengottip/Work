import csv
import os

def separate_records(csv_file, dir_file, file_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        for row in reader:
            if row[0].startswith('d'):
                with open(dir_file, 'a') as dir_output:
                    dir_output.write(','.join(row) + '\n')
            elif row[0].startswith('-'):
                with open(file_file, 'a') as file_output:
                    file_output.write(','.join(row) + '\n')

# Usage example
csv_file = 'H:/temp/input.csv'
dir_file = 'H:/temp/directories.csv'
file_file = 'H:/temp/files.csv'
separate_records(csv_file, dir_file, file_file)
# Move the files to the H:\temp directory
#os.rename(dir_file, 'H:/temp/' + dir_file)
#os.rename(file_file, 'H:/temp/' + file_file)
def sort_by_file_size(file_file):
    with open(file_file, 'r') as file:
        reader = csv.reader(file)
        rows = list(reader)
        sorted_rows = sorted(rows, key=lambda row: int(row[4]), reverse=True)
        for row in sorted_rows:
            print(','.join(row))

# Usage example
file_file = 'H:/temp/files.csv'
sort_by_file_size(file_file)



