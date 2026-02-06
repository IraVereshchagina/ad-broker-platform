CREATE TABLE ad_clicks (
    id VARCHAR(255) PRIMARY KEY,
    campaign_id VARCHAR(255) NOT NULL,
    short_code VARCHAR(50) NOT NULL,
    ip_address VARCHAR(50),
    country VARCHAR(10),
    user_agent TEXT,
    clicked_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_clicks_campaign ON ad_clicks(campaign_id);
CREATE INDEX idx_clicks_date ON ad_clicks(clicked_at);