-- Dados de teste para desenvolvimento local
-- Este arquivo é carregado automaticamente pelo Spring Boot quando usando H2

-- Inserir usuários de teste
INSERT INTO tb_usuario (nome, login, email, senha, telefone, cpf, data_nascimento, tipo_usuario, tipo_perfil, ativo, possui_loja, data_cadastro, email_verificado) VALUES
('João Silva', 'joao.silva', 'joao.silva@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EuHuDdpXy6pJ8IqSctUJm6', '11999999999', '12345678901', '1990-05-15', 'aluno', 'cliente', true, true, '2023-01-01 10:00:00', true),
('Maria Santos', 'maria.santos', 'maria.santos@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EuHuDdpXy6pJ8IqSctUJm6', '11888888888', '98765432109', '1985-03-22', 'professor', 'vendedor', true, false, '2023-01-02 11:00:00', true),
('Pedro Costa', 'pedro.costa', 'pedro.costa@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EuHuDdpXy6pJ8IqSctUJm6', '11777777777', '11122233344', '1992-07-10', 'aluno', 'cliente', true, false, '2023-01-03 12:00:00', true);

-- Inserir categorias
INSERT INTO tb_categoria (nome, descricao, icone) VALUES
('Lanches', 'Hambúrgueres, sanduíches e afins', 'fast-food'),
('Bebidas', 'Sucos, refrigerantes e bebidas', 'cafe'),
('Sobremesas', 'Doces e sobremesas', 'ice-cream'),
('Pratos Principais', 'Almoços e jantares', 'restaurant');

-- Inserir loja para o usuário 1 (João Silva)
INSERT INTO tb_loja (nome_fantasia, descricao, foto_capa, localizacao, status, visivel, aceita_pix, aceita_dinheiro, aceita_cartao, data_criacao, qtd_produtos_vendidos, avaliacao_media, id_usuario, manual_override, total_pedidos) VALUES
('Lanchonete do João', 'Os melhores lanches da região', 'https://example.com/loja1.jpg', 'Rua das Flores, 123', true, true, true, true, true, '2023-01-15 08:00:00', 0, 0.0, 1, false, 0);

-- Inserir produtos para a loja
INSERT INTO tb_produto (nome, descricao, preco, imagem, estoque, estoque_minimo, disponivel, data_cadastro, vezes_vendido, avaliacao_media, id_categoria, id_loja) VALUES
('X-Burguer Especial', 'Hambúrguer com carne, queijo, alface e tomate', 15.50, 'https://example.com/xburguer.jpg', 50, 5, true, '2023-01-16 09:00:00', 0, 0.0, 1, 1),
('Refrigerante Lata', 'Coca-Cola 350ml', 4.50, 'https://example.com/coca.jpg', 100, 10, true, '2023-01-16 09:15:00', 0, 0.0, 2, 1),
('Batata Frita', 'Porção de batata frita crocante', 8.00, 'https://example.com/batata.jpg', 30, 3, true, '2023-01-16 09:30:00', 0, 0.0, 1, 1);

-- Inserir horários de funcionamento para a loja
INSERT INTO tb_horario_funcionamento (dia_semana, turno, id_loja) VALUES
('SEGUNDA', 'MANHA', 1),
('SEGUNDA', 'TARDE', 1),
('TERCA', 'MANHA', 1),
('TERCA', 'TARDE', 1),
('QUARTA', 'MANHA', 1),
('QUARTA', 'TARDE', 1),
('QUINTA', 'MANHA', 1),
('QUINTA', 'TARDE', 1),
('SEXTA', 'MANHA', 1),
('SEXTA', 'TARDE', 1);