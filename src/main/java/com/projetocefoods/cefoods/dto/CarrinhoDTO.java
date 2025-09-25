package com.projetocefoods.cefoods.dto;

import java.util.List;

public class CarrinhoDTO {
    public static class Item {
        public Long produto_id;
        public String nome;
        public Double preco_unit;
        public Integer quantidade;
        public String foto;
        // NÃO precisa de id_loja aqui
    }
    public Long id_carrinho;
    public Long id_loja; // importante: id da loja do carrinho (null quando vazio)
    public List<Item> itens;
}
