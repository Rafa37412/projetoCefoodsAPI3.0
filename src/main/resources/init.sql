-- ============================================================
-- 🔄 DROP TABLES (para recriar do zero sem conflito)
-- ============================================================
-- teste
DROP TABLE IF EXISTS tbnota_anexo CASCADE;
DROP TABLE IF EXISTS tbnota CASCADE;
DROP TABLE IF EXISTS tbnotificacao CASCADE;
DROP TABLE IF EXISTS tbpedido_item CASCADE;
DROP TABLE IF EXISTS tbitempedido CASCADE;
DROP TABLE IF EXISTS tbcarrinho_item CASCADE;
DROP TABLE IF EXISTS tbcarrinho CASCADE;
DROP TABLE IF EXISTS tbavaliacao CASCADE;
DROP TABLE IF EXISTS tbcomentario CASCADE;
DROP TABLE IF EXISTS tbhorariofuncionamento CASCADE;
DROP TABLE IF EXISTS tbproduto CASCADE;
DROP TABLE IF EXISTS tb_categoria CASCADE;
DROP TABLE IF EXISTS tbpedido CASCADE;
DROP TABLE IF EXISTS tbloja CASCADE;
DROP TABLE IF EXISTS tbusuario CASCADE;

-- ============================================================
-- 👤 Usuário
-- ============================================================
CREATE TABLE tbusuario (
    idUsuario BIGINT PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(255),
    telefone VARCHAR(255),
    login VARCHAR(255),
    ativo BOOLEAN,
    possuiLoja BOOLEAN,
    tipoPerfil VARCHAR(255),
    tipoUsuario VARCHAR(255),
    emailVerificado BOOLEAN,
    chavePix VARCHAR(255),
    fotoPerfil VARCHAR(255),
    dataCadastro TIMESTAMP,
    dataNascimento DATE,
    ultimoAcesso TIMESTAMP,
    tokenRecuperacao VARCHAR(255)
);

-- ============================================================
-- 🏪 Loja
-- ============================================================
CREATE TABLE tbloja (
    idLoja BIGINT PRIMARY KEY,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    cnpj VARCHAR(255),
    endereco VARCHAR(255),
    telefone VARCHAR(255),
    email VARCHAR(255),
    ativa BOOLEAN,
    usuario_id BIGINT REFERENCES tbusuario(idUsuario)
);

-- ============================================================
-- 📂 Categoria
-- ============================================================
CREATE TABLE tb_categoria (
    idCategoria BIGINT PRIMARY KEY,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    icone VARCHAR(255)
);

-- ============================================================
-- 📦 Produto
-- ============================================================
CREATE TABLE tbproduto (
    idProduto BIGINT PRIMARY KEY,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    preco NUMERIC(19,2),
    estoque INT,
    categoria_id BIGINT REFERENCES tb_categoria(idCategoria),
    loja_id BIGINT REFERENCES tbloja(idLoja)
);

-- ============================================================
-- ⭐ Avaliação
-- ============================================================
CREATE TABLE tbavaliacao (
    id BIGINT PRIMARY KEY,
    estrelas INT,
    produto_id BIGINT REFERENCES tbproduto(idProduto),
    usuario_id BIGINT REFERENCES tbusuario(idUsuario)
);

-- ============================================================
-- 💬 Comentário
-- ============================================================
CREATE TABLE tbcomentario (
    id BIGINT PRIMARY KEY,
    texto VARCHAR(255),
    data TIMESTAMP,
    produto_id BIGINT REFERENCES tbproduto(idProduto),
    usuario_id BIGINT REFERENCES tbusuario(idUsuario)
);

-- ============================================================
-- 🛒 Carrinho
-- ============================================================
CREATE TABLE tbcarrinho (
    idCarrinho BIGINT PRIMARY KEY,
    usuario_id BIGINT REFERENCES tbusuario(idUsuario),
    loja_id BIGINT REFERENCES tbloja(idLoja),
    criadoEm TIMESTAMP
);

-- 🛍️ Item de Carrinho
CREATE TABLE tbcarrinho_item (
    idItem BIGINT PRIMARY KEY,
    carrinho_id BIGINT REFERENCES tbcarrinho(idCarrinho),
    produto_id BIGINT REFERENCES tbproduto(idProduto),
    quantidade INT
);

-- ============================================================
-- 📦 Pedido
-- ============================================================
CREATE TABLE tbpedido (
    idPedido BIGINT PRIMARY KEY,
    usuario_id BIGINT REFERENCES tbusuario(idUsuario),
    loja_id BIGINT REFERENCES tbloja(idLoja),
    status VARCHAR(255),
    dataPedido TIMESTAMP,
    valorTotal NUMERIC(19,2)
);

-- 📦 Item de Pedido
CREATE TABLE tbpedido_item (
    id BIGINT PRIMARY KEY,
    pedido_id BIGINT REFERENCES tbpedido(idPedido),
    produto_id BIGINT REFERENCES tbproduto(idProduto),
    quantidade INT,
    preco NUMERIC(19,2)
);

-- 📦 Item Pedido (modelo alternativo)
CREATE TABLE tbitempedido (
    id BIGINT PRIMARY KEY,
    pedido_id BIGINT REFERENCES tbpedido(idPedido),
    produto_id BIGINT REFERENCES tbproduto(idProduto),
    quantidade INT,
    preco NUMERIC(19,2)
);

-- ============================================================
-- 🧾 Nota Fiscal + Anexos
-- ============================================================
CREATE TABLE tbnota (
    idNota BIGINT PRIMARY KEY,
    pedido_id BIGINT REFERENCES tbpedido(idPedido),
    valor NUMERIC(19,2),
    data TIMESTAMP
);

CREATE TABLE tbnota_anexo (
    idAnexo BIGINT PRIMARY KEY,
    nota_id BIGINT REFERENCES tbnota(idNota),
    nomeArquivo VARCHAR(255),
    tipo VARCHAR(255),
    tamanho BIGINT
);

-- ============================================================
-- 🔔 Notificação
-- ============================================================
CREATE TABLE tbnotificacao (
    idNotificacao BIGINT PRIMARY KEY,
    usuario_id BIGINT REFERENCES tbusuario(idUsuario),
    mensagem VARCHAR(255),
    lida BOOLEAN,
    data TIMESTAMP
);

-- ============================================================
-- ⏰ Horário de Funcionamento
-- ============================================================
CREATE TABLE tbhorariofuncionamento (
    idHorario BIGINT PRIMARY KEY,
    loja_id BIGINT REFERENCES tbloja(idLoja),
    diaSemana VARCHAR(255),
    turno VARCHAR(255),
    horaAbertura TIMESTAMP,
    horaFechamento TIMESTAMP
);

-- ============================================================
-- 📥 Inserts Iniciais
-- ============================================================
-- Usuário Admin
INSERT INTO tbusuario (
    idUsuario, nome, email, senha, cpf, telefone, login, ativo,
    possuiLoja, tipoPerfil, tipoUsuario, emailVerificado,
    chavePix, fotoPerfil, dataCadastro, dataNascimento, ultimoAcesso, tokenRecuperacao
) VALUES (
    1, 'Administrador', 'admin@cefoods.com', '123456', '000.000.000-00',
    '(11) 99999-9999', 'admin', TRUE,
    TRUE, 'ADMIN', 'GERENTE', TRUE,
    'admin@pix.com', NULL, NOW(), '1990-01-01', NOW(), NULL
);

-- Loja de teste
INSERT INTO tbloja (
    idLoja, nome, descricao, cnpj, endereco, telefone, email, ativa, usuario_id
) VALUES (
    1, 'Loja Central', 'Loja de testes no sistema CEFOODS', '00.000.000/0001-00',
    'Rua Principal, 123 - Centro', '(11) 4002-8922', 'lojacentral@cefoods.com',
    TRUE, 1
);

-- Categoria inicial
INSERT INTO tb_categoria (
    idCategoria, nome, descricao, icone
) VALUES (
    1, 'Bebidas', 'Categoria de bebidas e refrigerantes', '🥤'
);

-- Produto inicial
INSERT INTO tbproduto (
    idProduto, nome, descricao, preco, estoque, categoria_id, loja_id
) VALUES (
    1, 'Refrigerante Cola 2L', 'Bebida gaseificada sabor cola', 8.99, 100, 1, 1
);

-- Pedido de exemplo
INSERT INTO tbpedido (
    idPedido, usuario_id, loja_id, status, dataPedido, valorTotal
) VALUES (
    1, 1, 1, 'PENDENTE', NOW(), 8.99
);

-- Item de pedido
INSERT INTO tbpedido_item (
    id, pedido_id, produto_id, quantidade, preco
) VALUES (
    1, 1, 1, 1, 8.99
);

-- Nota Fiscal
INSERT INTO tbnota (
    idNota, pedido_id, valor, data
) VALUES (
    1, 1, 8.99, NOW()
);

-- Notificação inicial
INSERT INTO tbnotificacao (
    idNotificacao, usuario_id, mensagem, lida, data
) VALUES (
    1, 1, 'Bem-vindo ao CEFOODS! Seu cadastro foi realizado com sucesso.', FALSE, NOW()
);
