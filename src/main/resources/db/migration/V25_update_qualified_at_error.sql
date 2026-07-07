UPDATE alerts
SET created_at = qualified_at - INTERVAL '1 hour'
WHERE created_at > qualified_at;