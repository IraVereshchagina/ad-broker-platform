CREATE TABLE targeting_rules (
    id BIGSERIAL PRIMARY KEY,
    campaign_id VARCHAR(36) NOT NULL REFERENCES campaigns(id),
    attribute VARCHAR(50) NOT NULL,
    operator VARCHAR(20) NOT NULL,
    rule_value VARCHAR(255) NOT NULL
);

CREATE INDEX idx_targeting_rules_campaign ON targeting_rules(campaign_id);