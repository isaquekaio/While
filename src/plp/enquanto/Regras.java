package plp.enquanto;

import java.util.ArrayList;
import java.util.List;

import plp.enquanto.Linguagem.*;
import plp.enquanto.parser.EnquantoBaseListener;
import plp.enquanto.parser.EnquantoParser.*;

import static java.lang.Integer.parseInt;

public class Regras extends EnquantoBaseListener {
	private final Leia leia;
	private final Skip skip;
	private final Propriedades valores;

	private Programa programa;

	public Regras() {
		leia = new Leia();
		skip = new Skip();
		valores = new Propriedades();
	}

	public Programa getPrograma() {
		return programa;
	}

	@Override
	public void exitBool(BoolContext ctx) {
		valores.insira(ctx, new Booleano("verdadeiro".equals(ctx.getText())));
	}

	@Override
	public void exitLeia(LeiaContext ctx) {
		valores.insira(ctx, leia);
	}
	//'se' booleano 'entao' comando ('senaose' booleano 'então' comando)* 'senao' comando
	@Override
	public void exitSe(SeContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano(0));
		final Comando entao = valores.pegue(ctx.comando(0));
		final List<Bool> senaose = valores.pegue(ctx.booleano(1));		//novo
		final List<Comando> comando = valores.pegue(ctx.comando(1));	//novo
		final Comando senao = valores.pegue(ctx.comando(2));
		valores.insira(ctx, new Se(condicao, entao, senaose, comando, senao));
	}

	// Novo - 'escolha' expressao ('caso' expressao ':' comando)*
	@Override 
	public void exitEscolha(EscolhaContext ctx) { 
		final Expressao expressao = valores.pegue(ctx.expressao(0));
		final List<Expressao> casos = valores.pegue(ctx.expressao(1));
		final List<Comando> comandos = valores.pegue(ctx.comando(1));
		valores.insira(ctx, new Escolha(expressao, casos, comandos));
	}

	@Override
	public void exitInteiro(InteiroContext ctx) {
		valores.insira(ctx, new Inteiro(parseInt(ctx.getText())));
	}

	@Override
	public void exitSkip(SkipContext ctx) {
		valores.insira(ctx, skip);
	}

	@Override
	public void exitEscreva(EscrevaContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, new Escreva(exp));
	}

	@Override
	public void exitPrograma(ProgramaContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		programa = new Programa(cmds);
		valores.insira(ctx, programa);
	}

	@Override
	public void exitId(IdContext ctx) {
		final String id = ctx.ID().getText();
		valores.insira(ctx, new Id(id));
	}

	@Override
	public void exitSeqComando(SeqComandoContext ctx) {
		final List<Comando> comandos = new ArrayList<>();
		for (ComandoContext c : ctx.comando()) {
			comandos.add(valores.pegue(c));
		}
		valores.insira(ctx, comandos);
	}

	@Override
	public void exitAtribuicao(AtribuicaoContext ctx) {
		final String id = ctx.ID(0).getText();
		final Expressao exp = valores.pegue(ctx.expressao(0));
		valores.insira(ctx, new Atribuicao(id, exp));
	}

	@Override
	public void exitBloco(BlocoContext ctx) {
		final List<Comando> cmds = valores.pegue(ctx.seqComando());
		valores.insira(ctx, new Bloco(cmds));
	}

	@Override
	public void exitOpBin(OpBinContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Expressao exp; 
		
		switch (op) {
			case "^" : exp = new ExpExpo(esq, dir); break;	//ok
			case "*" : exp = new ExpMult(esq, dir); break;
			case "/" : exp = new ExpDiv(esq, dir); break;	//ok
			case "-" : exp = new ExpSub(esq, dir); break;
			case "+" : exp = new ExpSoma(esq, dir); break;
			default  : exp = new ExpSoma(esq, dir); break;
		};
		valores.insira(ctx, exp);
	}

	@Override
	public void exitEnquanto(EnquantoContext ctx) {
		final Bool condicao = valores.pegue(ctx.booleano());
		final Comando comando = valores.pegue(ctx.comando());
		valores.insira(ctx, new Enquanto(condicao, comando));
	}

	// Novo 
	@Override 
	public void exitPara(ParaContext ctx) { 
		final String id = ctx.ID().getText();
		final Expressao de = valores.pegue(ctx.expressao(0));
		final Expressao ate = valores.pegue(ctx.expressao(1));
		final Expressao passo = valores.pegue(ctx.expressao(2));
		final Comando faca = valores.pegue(ctx.comando());
		valores.insira(ctx, new Para(id, de, ate, passo, faca));
	}

	@Override
	public void exitELogico(ELogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new ELogico(esq, dir));
	}

	// Novo
	@Override
	public void exitOuLogico(OuLogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new OuLogico(esq, dir));
	}

	// Novo
	@Override
	public void exitXouLogico(XouLogicoContext ctx) {
		final Bool esq = valores.pegue(ctx.booleano(0));
		final Bool dir = valores.pegue(ctx.booleano(1));
		valores.insira(ctx, new XouLogico(esq, dir));
	}

	@Override
	public void exitBoolPar(BoolParContext ctx) {
		final Bool booleano = valores.pegue(ctx.booleano());
		valores.insira(ctx, booleano);
	}

	@Override
	public void exitNaoLogico(NaoLogicoContext ctx) {
		final Bool b = valores.pegue(ctx.booleano());
		valores.insira(ctx, new NaoLogico(b));
	}

	@Override
	public void exitExpPar(ExpParContext ctx) {
		final Expressao exp = valores.pegue(ctx.expressao());
		valores.insira(ctx, exp);
	}

	@Override
	public void exitExiba(ExibaContext ctx) {
		final String t = ctx.TEXTO().getText();
		final String texto = t.substring(1, t.length() - 1);
		valores.insira(ctx, new Exiba(texto));
	}

	@Override
	public void exitOpRel(OpRelContext ctx) {
		final Expressao esq = valores.pegue(ctx.expressao(0));
		final Expressao dir = valores.pegue(ctx.expressao(1));
		final String op = ctx.getChild(1).getText();
		final Bool exp;
		switch (op) {
			case "="  : exp = new ExpIgual(esq, dir); break;
			case "<=" : exp = new ExpMenorIgual(esq, dir); break;
			// Novos
			case ">=" : exp = new ExpMaiorIgual(esq, dir); break; 	//ok
			case "<>" : exp = new ExpDiferente(esq, dir); break;	//ok
			case "<"  : exp = new ExpMenor(esq, dir); break;		//ok
			case ">"  : exp = new ExpMaior(esq, dir); break;		//ok
			default   : exp = new ExpIgual(esq, esq); break;
		};
		valores.insira(ctx, exp);
	}
}
