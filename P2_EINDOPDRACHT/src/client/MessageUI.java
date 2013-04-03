package client;

/**
 * Interface for message processing,
 * used by ConnectionWindow and ActionWindow
 * to handle chat traffic.
 * @author martijnbruning
 *
 */
interface MessageUI {

	public void addMessage(final String name, final String msg);
}


