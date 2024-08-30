WITH last_60_month_ends AS (
    SELECT DATE_FORMAT(ADD_MONTHS(LAST_DAY(CURRENT_DATE), -seq_number), 'yyyy-MM-dd') AS month_end_date
    FROM (
        SELECT posexplode(split(space(59), ' ')) AS (seq_number, x)
    ) AS seq
),
total_count AS (
    SELECT COUNT(*) AS total_rows
    FROM your_table_name
    WHERE period_Date IN (SELECT month_end_date FROM last_60_month_ends)
),
old_data_count AS (
    SELECT COUNT(*) AS old_rows
    FROM your_table_name
    WHERE period_Date < DATE_SUB(CURRENT_DATE, 45)
    AND period_Date IN (SELECT month_end_date FROM last_60_month_ends)
)
SELECT 
    (old_data_count.old_rows / total_count.total_rows) * 100 AS percentage_old_data
FROM 
    total_count, old_data_count;
