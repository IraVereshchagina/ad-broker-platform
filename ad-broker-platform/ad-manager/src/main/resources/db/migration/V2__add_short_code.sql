ALTER TABLE campaigns ADD COLUMN short_code VARCHAR(8);
CREATE UNIQUE INDEX idx_campaigns_short_code ON campaigns(short_code);