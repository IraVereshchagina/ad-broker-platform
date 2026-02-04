CREATE TABLE campaigns (
    id VARCHAR(36) PRIMARY KEY,
    author_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    ad_url TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    budget DECIMAL(19, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_campaigns_status ON campaigns(status);