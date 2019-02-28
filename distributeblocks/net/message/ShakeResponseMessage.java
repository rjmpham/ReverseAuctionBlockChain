package distributeblocks.net.message;

import distributeblocks.net.processor.AbstractMessageProcessor;
import distributeblocks.net.processor.ShakeResponseProcessor;

public class ShakeResponseMessage extends AbstractMessage {


	public String messsage;
	public int listeningPort;

	/**
	 *
	 * @param messsage
	 * @param listeningPort
	 *    The port that the sender is listening on.
	 */
	public ShakeResponseMessage(String messsage, int listeningPort) {
		this.messsage = messsage;
		this.listeningPort = listeningPort;
	}

	@Override
	public AbstractMessageProcessor getProcessor() {
		return new ShakeResponseProcessor();
	}
}