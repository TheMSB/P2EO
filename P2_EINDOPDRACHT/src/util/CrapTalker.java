package util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CrapTalker {

	private static PrintWriter pWriter;
	private static final String[] INSULTS_START = {"Govnodavy","Yob materi vashi!","Ti galuboy"};
	private static final String[] INSULTS_OPPONENT_MOVE = {"Govnosos","Pizdets","Do pizdy","Nu vse, tebe pizda","Durak neshtiasnyI","Viyebnutsa","Ti Durak","Na huy...?"};
	private static final String[] INSULTS_OPPONENT_LOSE = {"Perdoon stary","Poshol nahuj","Loh","Pidar","Eedee tryakhate tvayu mamu"};
	private static final String[] INSULTS_OPPONENT_WIN = {"Naveshat pizdyley","Pacheemu ti takoy galuboy","Otyebis ot menya!","Polniy pizdets"};
	private static final String[] INSULTS_ME_MOVE = {"Potselui mou zhopy","Zhri govno i zdohni!"};
	private static final String[] INSULTS_ME_LOSE = {"Hooy morzhovy","Yebat vashu mat","Zaebis"};
	private static final String[] INSULTS_ME_WIN = {"Naveshat pizdyley","Otsosi","Tvaya mat sasala mney"};
	private static final String[] INSULTS_OPPONENT_CHATS = {"Past' zabej, padla jebanaja","Zacroy svoy peesavati rot, sooka","Zacroy rot","Eto mnye do huya"};
	private static final String[] INSULTS_OPPONENT_DISCONNECT = {"Ne ssi v kompot, tam povor nogi moet"};

	public static void main(String[] args) {
		System.out.println(insult());
	}

	public static String Convert() throws FileNotFoundException {
		pWriter = new PrintWriter("RussianInsults.txt");
		pWriter.println("Кгыышфт еуче");
		pWriter.flush();

		return "Some russian text";
	}

	public static String toCyrillic(String string) {
		String output = string.toLowerCase();
		output = output.replaceAll("haha", "xaxa");
		output = output.replaceAll("xaha", "xaxa");
		output = output.replaceAll("ya", "�?");
		output = output.replaceAll("yu", "ю");
		output = output.replaceAll("sh", "щ");
		output = output.replaceAll("ch", "ч");
		output = output.replaceAll("ts", "ц");
		output = output.replaceAll("kh", "х");
		output = output.replaceAll("zh", "ж");
		output = output.replaceAll("yo", "ё");
		output = output.replaceAll("f", "ф");
		output = output.replaceAll("e", "e");
		output = output.replaceAll("y", "ы");
		output = output.replaceAll("u", "у");
		output = output.replaceAll("t", "т");
		output = output.replaceAll("s", "c");
		output = output.replaceAll("r", "р");
		output = output.replaceAll("p", "п");
		output = output.replaceAll("o", "o");
		output = output.replaceAll("n", "н");
		output = output.replaceAll("m", "м");
		output = output.replaceAll("l", "л");
		output = output.replaceAll("k", "к");
		output = output.replaceAll("j", "й");
		output = output.replaceAll("i", "и");
		output = output.replaceAll("z", "з");
		output = output.replaceAll("d", "д");
		output = output.replaceAll("g", "г");
		output = output.replaceAll("v", "в");
		output = output.replaceAll("b", "б");
		output = output.replaceAll("a", "a");
		return output;
	}

	public static String insult() {
		//Checked game state en zet dan insultType
		String[] insultType = INSULTS_START;
		String insult = insultType[(int)(Math.random()*insultType.length)];
		
		return toCyrillic(insult);
	}

}
