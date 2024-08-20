import csv

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
csv_file = 'input.csv'
dir_file = 'directories.txt'
file_file = 'files.txt'
separate_records(csv_file, dir_file, file_file)