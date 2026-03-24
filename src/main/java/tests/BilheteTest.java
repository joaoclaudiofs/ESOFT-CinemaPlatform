package tests;

import app.billing.BilheteOpções;
import models.*;
import models.sale.sellable.Combo;
import models.sale.sellable.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class BilheteTest {

    private AppData appData;

    @BeforeEach
    public void setup() {
        appData = AppData.getInstance();
        assertFalse(appData.getFilms().isEmpty(), "Nenhum filme carregado");
        assertFalse(appData.getSessions().isEmpty(), "Nenhuma sessão carregada");
        assertFalse(appData.getCombos().isEmpty(), "Nenhum combo carregado");
    }

    @Test
    public void testAdicionarBilheteComCombo() {
        AtomicReference<Ticket> ticketRef = new AtomicReference<>();

        BilheteOpções janela = new BilheteOpções(ticketRef::set);

        Film filme = appData.getFilms().get(0);
        List<Session> sessoesFilme = appData.getSessions().stream()
                .filter(s -> s.getFilm().equals(filme))
                .toList();

        assertFalse(sessoesFilme.isEmpty(), "Nenhuma sessão para o filme");

        Session sessao = sessoesFilme.get(0);
        String lugar = sessao.getAvailableSeats().length > 0 ? sessao.getAvailableSeats()[0] : null;
        assertNotNull(lugar, "Nenhum lugar disponível");

        janela.comboBox3.setSelectedItem(filme.getName());
        janela.comboBox1.setSelectedItem(sessao.getHorario());
        janela.comboBox2.setSelectedItem(lugar);

        janela.normalRadioButton.setSelected(true);
        janela.updateCombos();

        Enumeration<AbstractButton> buttons = janela.comboGroup.getElements();
        JRadioButton firstComboButton = null;
        while (buttons.hasMoreElements()) {
            JRadioButton btn = (JRadioButton) buttons.nextElement();
            if (btn.isEnabled()) {
                firstComboButton = btn;
                break;
            }
        }
        assertNotNull(firstComboButton, "Nenhum combo disponível para seleção");
        firstComboButton.setSelected(true);

        janela.adicionarBilhete(null);

        Ticket resultado = ticketRef.get();
        assertNotNull(resultado, "Callback não foi chamado.");
        assertEquals(sessao, resultado.getSession());
        assertEquals(Ticket.Type.NORMAL, resultado.getType());
        assertNotNull(resultado.getCombo(), "Nenhum combo foi selecionado");
    }

    @Test
    public void testAdicionarBilheteSemCombo() {
        AtomicReference<Ticket> ticketRef = new AtomicReference<>();

        BilheteOpções janela = new BilheteOpções(ticketRef::set);

        Film filme = appData.getFilms().get(0);
        List<Session> sessoesFilme = appData.getSessions().stream()
                .filter(s -> s.getFilm().equals(filme))
                .toList();

        assertFalse(sessoesFilme.isEmpty(), "Nenhuma sessão para o filme");

        Session sessao = sessoesFilme.get(0);
        String lugar = sessao.getAvailableSeats()[0];

        janela.comboBox3.setSelectedItem(filme.getName());
        janela.comboBox1.setSelectedItem(sessao.getHorario());
        janela.comboBox2.setSelectedItem(lugar);

        janela.normalRadioButton.setSelected(true);
        janela.updateCombos();

        janela.adicionarBilhete(null);

        Ticket resultado = ticketRef.get();
        assertNotNull(resultado, "Callback não foi chamado.");
        assertEquals(sessao, resultado.getSession());
        assertEquals(Ticket.Type.NORMAL, resultado.getType());
        assertNull(resultado.getCombo(), "Combo deveria ser null");
    }
}
