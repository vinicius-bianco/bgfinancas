/*
Copyright 2012-2018 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

package io.github.badernageral.bgfinancas.principal;

import io.github.badernageral.bgfinancas.biblioteca.ajuda.Ajuda;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador;
import static io.github.badernageral.bgfinancas.biblioteca.contrato.Controlador.idioma;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Tabela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Posicao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Numeros;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Agenda;
import io.github.badernageral.bgfinancas.modelo.Conta;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import io.github.badernageral.bgfinancas.modelo.Grupo;
import io.github.badernageral.bgfinancas.modelo.Planejamento;
import io.github.badernageral.bgfinancas.modelo.Receita;
import io.github.badernageral.bgfinancas.modelo.Transferencia;
import io.github.badernageral.bgfinancas.modulo.agenda.AgendaFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.conta.ContaFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.despesa.DespesaFormularioControlador;
import io.github.badernageral.bgfinancas.modulo.receita.ReceitaFormularioControlador;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public final class HomeControlador implements Initializable, Controlador {
    
    @FXML private GridPane home;
    
    @FXML private GridPane gridContas;
    @FXML private GridPane gridRelatorio;
    @FXML private GridPane gridPlanejamento;
    @FXML private GridPane gridAgenda;
    
    // Contas
    @FXML private Label labelContas;
    @FXML private Label labelCreditoTotal;
    @FXML private Label labelPoupancaTotal;
    @FXML private TableView<Conta> tabelaListaConta;
    private final Tabela<Conta> tabelaConta = new Tabela<>();
    
    // Relatório
    @FXML private Label labelRelatorio;
    @FXML private Label labelRelatorioData;
    @FXML private Label labelValorTotalRelatorio;
    @FXML private TabPane painelRelatorio;
    @FXML private Tab tabRelatorioDespesas;
    @FXML private Tab tabRelatorioReceitas;
    @FXML private Tab tabRelatorioTransferencias;
    @FXML private Tab tabRelatorioGrupos;
    @FXML private Tab tabRelatorioDespesasAgendadas;
    @FXML private TableView<Despesa> tabelaListaRelatorioDespesas;
    @FXML private TableView<Receita> tabelaListaRelatorioReceitas;
    @FXML private TableView<Transferencia> tabelaListaRelatorioTransferencias;
    @FXML private TableView<Grupo> tabelaListaRelatorioGrupos;
    @FXML private TableView<Despesa> tabelaListaRelatorioDespesasAgendadas;
    private final Tabela<Despesa> tabelaRelatorioDespesas = new Tabela<>();
    private final Tabela<Receita> tabelaRelatorioReceitas = new Tabela<>();
    private final Tabela<Transferencia> tabelaRelatorioTransferencias = new Tabela<>();
    private final Tabela<Grupo> tabelaGrupos = new Tabela<>();
    private final Tabela<Despesa> tabelaRelatorioDespesasAgendadas = new Tabela<>();
    private LocalDate dataRelatorio = LocalDate.now();
    private BigDecimal valorTotalRelatorio = BigDecimal.ZERO;
    
    // Planejamento
    @FXML private Label labelPlanejamento;
    @FXML private Label labelPlanejamentoData;
    @FXML private Label labelPlanejamentoTotalDespesas;
    @FXML private Label labelPlanejamentoTotalReceitas;
    @FXML private Label labelPlanejamentoSaldo;
    @FXML private TableView<Planejamento> tabelaListaPlanejamento;
    private final Tabela<Planejamento> tabelaPlanejamento = new Tabela<>();
    
    // Agenda
    @FXML private Label labelAgenda;
    @FXML private Label labelValorTotalAgenda;
    @FXML private TableView<Agenda> tabelaListaAgenda;
    private BigDecimal valorTotalAgenda;
    private final Tabela<Agenda> tabelaAgenda = new Tabela<>();
    
    // Outros
    private LocalDate data = LocalDate.now();
    private BigDecimal valorCredito;
    private BigDecimal valorPoupanca;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Kernel.setTitulo(Linguagem.getInstance().getMensagem("principal"));
        inicializarContas();
        inicializarRelatorio();
        inicializarPlanejamento();
        inicializarAgenda();
        atualizarTabela(true);
    }
    
    private void inicializarContas(){
        tabelaConta.prepararTabela(tabelaListaConta, 1);
        prepararColunaTipoConta(tabelaConta.adicionarColuna(tabelaListaConta, "", "saldoTotal"));
        tabelaConta.adicionarColuna(tabelaListaConta, idioma.getMensagem("nome"), "nome");
        tabelaConta.setColunaColorida(tabelaConta.adicionarColunaNumero(tabelaListaConta, idioma.getMensagem("saldo"), "valor"));
        labelContas.setText(idioma.getMensagem("contas"));        
    }
    
    private void prepararColunaTipoConta(TableColumn colunaTipoConta){
        colunaTipoConta.setMinWidth(35);
        colunaTipoConta.setMaxWidth(35);
        colunaTipoConta.setCellFactory(coluna -> {
            return new TableCell<Conta, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        if(item.equals(idioma.getMensagem("credito"))){
                            setStyle("-fx-background-image: url(\"io/github/badernageral/bgfinancas/recursos/imagem/outros/credito.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center");
                        }else{
                            setStyle("-fx-background-image: url(\"io/github/badernageral/bgfinancas/recursos/imagem/outros/poupanca.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center");
                        }
                    }
                }
            };
        });
    }
    
    private void inicializarPlanejamento(){
        labelPlanejamento.setText(idioma.getMensagem("planejamento"));
        tabelaPlanejamento.prepararTabela(tabelaListaPlanejamento, 2);
        Planejamento.prepararColunaTipo(tabelaPlanejamento.adicionarColuna(tabelaListaPlanejamento, "", "isDespesa"));
        tabelaPlanejamento.adicionarColuna(tabelaListaPlanejamento, idioma.getMensagem("nome"), "nomeItem").setMinWidth(150);
        tabelaPlanejamento.adicionarColunaNumero(tabelaListaPlanejamento, idioma.getMensagem("valor"), "valor");
        tabelaPlanejamento.adicionarColunaData(tabelaListaPlanejamento, idioma.getMensagem("data"), "data");
    }
    
    private void inicializarAgenda(){
        tabelaAgenda.prepararTabela(tabelaListaAgenda, 6);
        tabelaAgenda.adicionarColuna(tabelaListaAgenda, idioma.getMensagem("tipo"), "nomeCategoria");
        tabelaAgenda.adicionarColuna(tabelaListaAgenda, idioma.getMensagem("descricao"), "nome");
        tabelaAgenda.adicionarColunaData(tabelaListaAgenda, idioma.getMensagem("data"), "data");
        tabelaAgenda.setColunaColorida(tabelaAgenda.adicionarColunaNumero(tabelaListaAgenda, idioma.getMensagem("valor"), "valor"));
        labelAgenda.setText(idioma.getMensagem("lembretes"));
    }
    
    private void inicializarRelatorio(){
        painelRelatorio.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            atualizarValorTotalRelatorio(newValue);
        });
        labelRelatorio.setText(idioma.getMensagem("mini_relatorio_mensal"));
        tabRelatorioDespesas.setText(idioma.getMensagem("despesas"));
        Image imagemDespesas = new Image(Kernel.RAIZ+"/recursos/imagem/modulo/icon/despesas.png");
        ImageView iconeDespesas = new ImageView(imagemDespesas);
        tabRelatorioDespesas.setGraphic(iconeDespesas);
        tabRelatorioReceitas.setText(idioma.getMensagem("receitas"));
        Image imagemReceitas = new Image(Kernel.RAIZ+"/recursos/imagem/modulo/icon/receitas.png");
        ImageView iconeReceitas = new ImageView(imagemReceitas);
        tabRelatorioReceitas.setGraphic(iconeReceitas);
        tabRelatorioTransferencias.setText(idioma.getMensagem("transferencias"));
        Image imagemTransferencias = new Image(Kernel.RAIZ+"/recursos/imagem/modulo/icon/transferencias.png");
        ImageView iconeTransferencias = new ImageView(imagemTransferencias);
        tabRelatorioTransferencias.setGraphic(iconeTransferencias);
        tabRelatorioGrupos.setText(idioma.getMensagem("cotas_despesas"));
        Image imagemGrupos = new Image(Kernel.RAIZ+"/recursos/imagem/modulo/icon/grupos.png");
        ImageView iconeGrupos = new ImageView(imagemGrupos);
        tabRelatorioGrupos.setGraphic(iconeGrupos);
        tabRelatorioDespesasAgendadas.setText(idioma.getMensagem("despesas_agendadas"));
        Image imagemPlanejamento = new Image(Kernel.RAIZ+"/recursos/imagem/modulo/icon/planejamento_mini.png");
        ImageView iconePlanejamento = new ImageView(imagemPlanejamento);
        tabRelatorioDespesasAgendadas.setGraphic(iconePlanejamento);
        tabelaRelatorioDespesas.prepararTabela(tabelaListaRelatorioDespesas, 3);
        tabelaRelatorioDespesas.adicionarColuna(tabelaListaRelatorioDespesas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioDespesas.setColunaColorida(tabelaRelatorioDespesas.adicionarColunaNumero(tabelaListaRelatorioDespesas, idioma.getMensagem("valor"), "valor"));
        tabelaRelatorioReceitas.prepararTabela(tabelaListaRelatorioReceitas, 3);
        tabelaRelatorioReceitas.adicionarColuna(tabelaListaRelatorioReceitas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioReceitas.setColunaColorida(tabelaRelatorioReceitas.adicionarColunaNumero(tabelaListaRelatorioReceitas, idioma.getMensagem("valor"), "valor"));
        tabelaRelatorioTransferencias.prepararTabela(tabelaListaRelatorioTransferencias, 3);
        tabelaRelatorioTransferencias.adicionarColuna(tabelaListaRelatorioTransferencias, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioTransferencias.setColunaColorida(tabelaRelatorioTransferencias.adicionarColunaNumero(tabelaListaRelatorioTransferencias, idioma.getMensagem("valor"), "valor"));
        tabelaGrupos.prepararTabela(tabelaListaRelatorioGrupos, 3);
        tabelaGrupos.adicionarColuna(tabelaListaRelatorioGrupos, idioma.getMensagem("cota"), "nome");
        tabelaGrupos.adicionarColunaNumero(tabelaListaRelatorioGrupos, idioma.getMensagem("valor"), "valor");
        tabelaGrupos.setColunaColorida(tabelaGrupos.adicionarColunaNumero(tabelaListaRelatorioGrupos, idioma.getMensagem("saldo"), "saldo"));
        tabelaRelatorioDespesasAgendadas.prepararTabela(tabelaListaRelatorioDespesasAgendadas, 3);
        tabelaRelatorioDespesasAgendadas.adicionarColuna(tabelaListaRelatorioDespesasAgendadas, idioma.getMensagem("categoria"), "nomeCategoria");
        tabelaRelatorioDespesasAgendadas.setColunaColorida(tabelaRelatorioDespesasAgendadas.adicionarColunaNumero(tabelaListaRelatorioDespesasAgendadas, idioma.getMensagem("valor"), "valor"));
    }
    
    private void calcularSaldoContas(){
        valorPoupanca = tabelaListaConta.getItems().stream()
                .filter(c -> c.getSaldoTotal().equals(idioma.getMensagem("poupanca")))
                .map(c -> c.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valorCredito = tabelaListaConta.getItems().stream()
                .filter(c -> c.getSaldoTotal().equals(idioma.getMensagem("credito")))
                .map(c -> c.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labelPoupancaTotal.setText(idioma.getMensagem("total_poupanca")+": "+idioma.getMensagem("moeda")+" "+valorPoupanca);
        labelCreditoTotal.setText(idioma.getMensagem("total_credito")+": "+idioma.getMensagem("moeda")+" "+valorCredito);
    }
    
    private void calcularValorPlanejamento(){
        BigDecimal totalReceitas = tabelaListaPlanejamento.getItems().stream()
                .filter(p -> !p.isDespesa())
                .map(d -> d.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDespesas = tabelaListaPlanejamento.getItems().stream()
                .filter(p -> p.isDespesa())
                .map(d -> d.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        labelPlanejamentoTotalReceitas.setText(idioma.getMensagem("receitas") + ": " + idioma.getMensagem("moeda") + " " + totalReceitas.toString());
        labelPlanejamentoTotalDespesas.setText(idioma.getMensagem("despesas") + ": " + idioma.getMensagem("moeda") + " " + totalDespesas.toString());
        labelPlanejamentoSaldo.setText(idioma.getMensagem("saldo") + ": " + idioma.getMensagem("moeda") + " " + Numeros.arredondar(totalReceitas.subtract(totalDespesas)));
    }
    
    public void atualizarTabela(boolean animacao){
        atualizarContas();
        atualizarRelatorio();
        atualizarPlanejamento();
        atualizarAgenda();
        if(animacao){
            Animacao.fadeOutIn(home);
        }
    }
    
    private void atualizarContas(){
        tabelaListaConta.setItems(new Conta().setAtivada("1").listar());
        calcularSaldoContas();
    }
    
    private void atualizarRelatorio(){
        labelRelatorioData.setText(idioma.getNomeMes(dataRelatorio.getMonthValue()).substring(0,3)+" / "+dataRelatorio.getYear());
        tabelaListaRelatorioDespesas.setItems(new Despesa().getRelatorioMensal(dataRelatorio));
        tabelaListaRelatorioReceitas.setItems(new Receita().getRelatorioMensal(dataRelatorio));
        tabelaListaRelatorioTransferencias.setItems(new Transferencia().getRelatorioMensal(dataRelatorio));
        tabelaListaRelatorioGrupos.setItems(new Grupo().getRelatorio(dataRelatorio, null));
        tabelaListaRelatorioDespesasAgendadas.setItems(new Despesa().getRelatorioMensal(dataRelatorio,true));
        atualizarValorTotalRelatorio(painelRelatorio.getSelectionModel().getSelectedIndex());
    }
    
    private void atualizarValorTotalRelatorio(Number aba){
        labelValorTotalRelatorio.setVisible(true);
        valorTotalRelatorio = BigDecimal.ZERO;
        switch(aba.intValue()){
            case 1:
                tabelaListaRelatorioDespesas.getItems().stream().forEach(item -> {
                    valorTotalRelatorio = valorTotalRelatorio.add(item.getValor());
                });
                break;
            case 2:
                tabelaListaRelatorioReceitas.getItems().stream().forEach(item -> {
                    valorTotalRelatorio = valorTotalRelatorio.add(item.getValor());
                });
                break;
            case 3:
                tabelaListaRelatorioTransferencias.getItems().stream().forEach(item -> {
                    valorTotalRelatorio = valorTotalRelatorio.add(item.getValor());
                });
                break;
            default:
                labelValorTotalRelatorio.setVisible(false);
                break;
        }
        labelValorTotalRelatorio.setText(idioma.getMensagem("moeda")+" "+valorTotalRelatorio.toString());
    }
    
    public void atualizarPlanejamento(){
        tabelaListaPlanejamento.setItems(new Planejamento().listar(data.getMonthValue(), data.getYear(), null));
        labelPlanejamentoData.setText(idioma.getNomeMes(data.getMonthValue()).substring(0,3)+" / "+data.getYear());
        calcularValorPlanejamento();
    }
    
    private void atualizarAgenda(){
        valorTotalAgenda = BigDecimal.ZERO;
        tabelaListaAgenda.setItems(new Agenda().listar());
        tabelaListaAgenda.getItems().stream().forEach(item -> {
            valorTotalAgenda = valorTotalAgenda.add(item.getValor());
        });
        labelValorTotalAgenda.setText(idioma.getMensagem("moeda")+" "+valorTotalAgenda.toString());
    }
    
    public void proximoMesRelatorio(){
        dataRelatorio = dataRelatorio.plusMonths(1);
        atualizarRelatorio();
    }
    
    public void anteriorMesRelatorio(){
        dataRelatorio = dataRelatorio.minusMonths(1);
        atualizarRelatorio();
    }
    
    public void proximoMesDespesas(){
        data = data.plusMonths(1);
        atualizarPlanejamento();
    }
    
    public void anteriorMesDespesas(){
        data = data.minusMonths(1);
        atualizarPlanejamento();
    }
    
    public void acaoContas(){
        Kernel.principal.acaoConta();
    }
    
    public void acaoRelatorios(){
        Kernel.principal.acaoRelatorios();
    }
    
    public void acaoPlanejamento(){
        Kernel.principal.acaoPlanejamento();
    }
    
    public void acaoAgenda(){
        Kernel.principal.acaoAgenda();
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoAlterar(int tabela) {
        switch (tabela) {
            case 1:
                {
                    ObservableList<Conta> itens = tabelaListaConta.getSelectionModel().getSelectedItems();
                    ContaFormularioControlador Controlador = Janela.abrir(Conta.FXML_FORMULARIO, idioma.getMensagem("contas"));
                    Controlador.alterar(itens.get(0));
                    break;
                }
            case 2:
                {
                    ObservableList<Planejamento> itens = tabelaListaPlanejamento.getSelectionModel().getSelectedItems();
                    if(itens.get(0).isDespesa()){
                        DespesaFormularioControlador Controlador = Janela.abrir(Despesa.FXML_FORMULARIO, idioma.getMensagem("planejamento"));
                        Controlador.alterar(itens.get(0).getDespesa());
                    }else{
                        ReceitaFormularioControlador Controlador = Janela.abrir(Receita.FXML_FORMULARIO, idioma.getMensagem("planejamento"));
                        Controlador.alterar(itens.get(0).getReceita());
                    }
                    break;
                }
            case 6:
                {
                    ObservableList<Agenda> itens = tabelaListaAgenda.getSelectionModel().getSelectedItems();
                    AgendaFormularioControlador Controlador = Janela.abrir(Agenda.FXML_FORMULARIO, idioma.getMensagem("lembretes"));
                    Controlador.alterar(itens.get(0));
                    break;
                }
            default:
                System.out.println(idioma.getMensagem("nao_implementado")+tabela);
                break;
        }
    }

    @Override
    public void acaoExcluir(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoGerenciar(int botao) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoVoltar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void acaoFiltrar(Boolean animacao) {
        atualizarTabela(true);
    }

    @Override
    public void acaoAjuda() {
        Ajuda.getInstance().setObjetos(gridContas, gridRelatorio, gridPlanejamento, gridAgenda);
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_home_1"));
        Ajuda.getInstance().capitulo(Posicao.CENTRO, idioma.getMensagem("tuto_home_2"));
        Ajuda.getInstance().capitulo(Posicao.TOPO, idioma.getMensagem("tuto_home_3"));
        Ajuda.getInstance().capitulo(gridContas, Posicao.BAIXO, idioma.getMensagem("tuto_home_4"));
        Ajuda.getInstance().capitulo(gridRelatorio, Posicao.BAIXO, idioma.getMensagem("tuto_home_5"));
        Ajuda.getInstance().capitulo(gridPlanejamento, Posicao.TOPO, idioma.getMensagem("tuto_home_6"));
        Ajuda.getInstance().capitulo(gridAgenda, Posicao.TOPO, idioma.getMensagem("tuto_home_7"));
        Ajuda.getInstance().apresentarProximo();
    }
    
}
