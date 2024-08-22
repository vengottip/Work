WITH total_count AS (
    SELECT COUNT(*) AS total_rows
    FROM your_table_name
),
old_data_count AS (
    SELECT COUNT(*) AS old_rows
    FROM your_table_name
    WHERE period_Date < DATE_SUB(CURRENT_DATE, 45)
)
SELECT 
    (old_data_count.old_rows / total_count.total_rows) * 100 AS percentage_old_data
FROM 
    total_count, old_data_count;