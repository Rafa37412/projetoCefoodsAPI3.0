-- Ensure produto images are stored as TEXT to support Base64 payloads
ALTER TABLE tb_produto
    ALTER COLUMN imagem TYPE TEXT
    USING imagem::text;
