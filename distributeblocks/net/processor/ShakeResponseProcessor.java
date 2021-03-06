package distributeblocks.net.processor;

import distributeblocks.io.ConfigManager;
import distributeblocks.net.NetworkService;
import distributeblocks.net.message.RequestPeersMessage;
import distributeblocks.net.message.ShakeResponseMessage;
import distributeblocks.io.Console;

public class ShakeResponseProcessor extends AbstractMessageProcessor<ShakeResponseMessage> {
	@Override
	public void processMessage(ShakeResponseMessage message) {
		Console.log("Got shake response: " + message.messsage);
		message.senderNode.setListenPort(message.listeningPort);
		RequestPeersMessage requestPeersMessage;

		if (message.letsBeFriends && NetworkService.getNetworkManager().needMorePeers() &&
				!NetworkService.getNetworkManager().isConnectedToNode(message.senderNode.getListeningAddress()) &&
				!message.seedNode){
			// Dont do anything, maintain connection?
			// Add it to the node config list silly!
			Console.log("Adding a new friend: " + message.senderNode.getListeningAddress());
			ConfigManager configManager = new ConfigManager();
			configManager.addNodeAndWrite(message.senderNode);
			NetworkService.getNetworkManager().addNode(message.senderNode);
			NetworkService.getNetworkManager().removeTemporaryNode(message.senderNode);
			requestPeersMessage = new RequestPeersMessage(true);
		} else {
			requestPeersMessage = new RequestPeersMessage(false);
		}

		// If the other node wants to be friends or not, send a peer info request.
		// The node will get removed from the temporary pool in the PeerInfoProcessor
		message.senderNode.asyncSendMessage(requestPeersMessage);

		if (!message.seedNode) {
			//NetworkService.getNetworkManager().removeTemporaryNode(message.senderNode);
			// Dont remove from temporary, do that in the peerinfo processor
		}

	}
}
