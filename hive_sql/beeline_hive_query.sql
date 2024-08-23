SELECT table_name, column_name
FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = 'your_database_name'
AND table_name IN ('table1', 'table2', 'table3')  -- List your specific table names here
AND (column_name LIKE '%dt%' OR column_name LIKE '%date%');
