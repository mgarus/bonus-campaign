INSERT INTO campaign (id, uuid, name, start_date, end_date, amount)
VALUES
(1, 'd41d8cd9-8f00-3200-b398-4125024a33c1', 'Good Campaign', '2024-03-22', '2024-03-29', 1000.00),
(2, 'd41d8cd9-8f00-3200-b398-4125024a33c2', 'Future Campaign', '2024-04-22', '2024-04-29', 1500.50),
(3, 'd41d8cd9-8f00-3200-b398-4125024a33c3', 'Campaign 3', '2024-03-01', '2024-04-30', 2000.75);
--
INSERT INTO condition (id, campaign_id, name, valuee)
VALUES
(1, 1, 'customerUuid', '123e4567-e89b-12d3-a456-556642440000'),
(2, 2, 'customerUuid', '123e4567-e89b-12d3-a456-556642440000'),
(3, 1, 'country', 'PL'),
(4, 1, 'minimumDepositAmount', '10'),
(5, 3, 'Condition 1', 'Value 4');
--
INSERT INTO offer (id, campaign_id, offer_uuid, expiration_date)
VALUES
(1, 1, 'd41d8cd9-8f00-3200-b398-4125024a33c1', '2024-12-31'),
(2, 2, 'd41d8cd9-8f00-3200-b398-4125024a33c2', '2024-03-31'),
(3, 3, 'd41d8cd9-8f00-3200-b398-4125024a33c3', '2024-04-30');