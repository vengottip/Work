def join_files(base_name, output_file):
    with open(output_file, 'wb') as out:
        i = 0
        while True:
            part_name = f"{base_name}.part{i:03}"
            if not os.path.exists(part_name):
                break
            with open(part_name, 'rb') as part:
                out.write(part.read())
            i += 1

join_files("gemma-2b.zip", "gemma-2b-joined.zip")
