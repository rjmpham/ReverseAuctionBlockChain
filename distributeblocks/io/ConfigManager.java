package distributeblocks.io;

import distributeblocks.Block;
import distributeblocks.Node;
import distributeblocks.net.IPAddress;
import distributeblocks.net.NetworkService;
import distributeblocks.net.PeerNode;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

/**
 * Reading and writing to config files goes here.
 */
public class ConfigManager {

	//private static final String PEER_CONFIG_FILE = "./peer_config.txt";


	public ConfigManager() {

		// Temporary
		//ArrayList<PeerNode> peerNodes = new ArrayList<>();
		//peerNodes.add(new PeerNode(new IPAddress("localhost", 5833)));

		//writePeerNodes(peerNodes);
	}

	/**
	 * Reads peer node data from a config file.
	 * Creates the config file if it does not exist.
	 * 
	 * @return
	 *   All known peer nodes from the config file.
	 */
	public ArrayList<PeerNode> readPeerNodes(){

		Gson gson = new Gson();
		File file = new File(Node.PEER_CONFIG_FILE);

		if (!file.exists()){
			file = createPeerConfigFile();
		}

		String json = "";
		IPAddress[] nodes;

		try (Scanner scanner = new Scanner(file)){

			while (scanner.hasNextLine()){
				// Use  stringbuilder maybe.
				json += scanner.nextLine();
			}

			nodes = gson.fromJson(json, IPAddress[].class);

		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Could not read the peer node config file.");
		}

		if (nodes != null) {

			ArrayList<PeerNode> peerNodes = new ArrayList<PeerNode>();

			for (IPAddress ip : nodes){
				peerNodes.add(new PeerNode(ip));
			}

			return peerNodes;

		} else return new ArrayList<>();
	}


	public void writePeerNodes(ArrayList<PeerNode> peerNodes){

		IPAddress[] peers = new IPAddress[peerNodes.size()];

		for (int i = 0; i < peerNodes.size(); i ++){
			peers[i] = peerNodes.get(i).getListeningAddress();
		}

		Gson gson = new Gson();
		File file = new File(Node.PEER_CONFIG_FILE);

		if (!file.exists()){
			file = createPeerConfigFile();
		}
		
		try (PrintWriter writer = new PrintWriter(file)){
			
			String json = gson.toJson(peers);
			writer.write(json);
			
		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Could not write to peer node config file");
		}
		
	}


	/**
	 * Adds a node to the list of peer nodes,
	 * if the node already exists in the list, then does nothing.
	 *
	 * @param node
	 */
	public void addNodeAndWrite(PeerNode node){

		ArrayList<PeerNode> nodes = this.readPeerNodes();

		System.out.println("Adding node: " + node.getListeningAddress());

		boolean found = false;
		for (PeerNode n : nodes){
			if (n.equals(node)){
				found = true;
				break;
			}
		}

		if (!found){

			if (NetworkService.getNetworkManager().inSeedMode()){
				node.setAddress(node.getLocalAddress());
			}

			nodes.add(node);
			this.writePeerNodes(nodes);
		}
	}

	/**
	 * Removes the node from the config list and writes.
	 * If node isnt in list, does nothing.
	 *
	 * @param node
	 */
	public void removeNodeAndWrite(PeerNode node){

		ArrayList<PeerNode> nodes = this.readPeerNodes();
		nodes.remove(node);
		this.writePeerNodes(nodes);
	}



	public synchronized void saveBlockChain(ArrayList<LinkedList<Block>> blockChain){

		//Gson gson = new Gson();
		File file = new File(Node.BLOCKCHAIN_FILE);

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Node.BLOCKCHAIN_FILE))){

			//String json = gson.toJson(blockChain);
			out.writeObject(blockChain);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not save blockchain to file.");
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public synchronized ArrayList<LinkedList<Block>> loadBlockCHain(){

		Gson gson = new Gson();
		File file = new File(Node.BLOCKCHAIN_FILE);

		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not create blockchain file!");
			}

			// Create new chain with genisis node.
			ArrayList<LinkedList<Block>>chain = new ArrayList<>();
			LinkedList newFork = new LinkedList();


			newFork.add(Block.getGenisisBlock());
			chain.add(newFork);
			saveBlockChain(chain);

			//save(generateTestChain()); // TESTING ONLY.
		}

		String json = "";
		ArrayList<LinkedList<Block> > blockChain = new ArrayList<>();

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(Node.BLOCKCHAIN_FILE))){

			blockChain = (ArrayList<LinkedList<Block> >) in.readObject();
			return blockChain;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("could not read the blockchain file.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Tries to create the peer config file.
	 *
	 * @return
	 *   File object for the peer config file.
	 */
	private File createPeerConfigFile(){

		File  file = new File(Node.PEER_CONFIG_FILE);

		if (file.exists()){
			return file;
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// We dont need anything fancier than this..
		if (!file.exists()){
			throw new RuntimeException("Could not create peer config file.");
		}

		return file;
	}


	

}
