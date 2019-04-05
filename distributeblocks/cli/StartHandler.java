package distributeblocks.cli;

import java.util.concurrent.Callable;

import distributeblocks.Node;
import distributeblocks.io.Console;
import distributeblocks.net.IPAddress;
import distributeblocks.net.NetworkConfig;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(description = "Start network connection",
		 name = "start", mixinStandardHelpOptions = true)
public class StartHandler implements Callable<Void> {
	private Node node;
	
	@Option(names = {"-minp", "--minpeers"}, 
			description = "The minimum number of peers to connecto to")
	private int minPeers = 3;
	
	@Option(names = {"-maxp", "--maxpeers"}, 
			description = "The maximum number of peers to connecto to")
	private int maxPeers = 10;
	
	@Option(names = {"-p", "--port"}, 
			description = "The port to open on")
	private int port = 5833;
	
	@Option(names = {"-sAddr", "--seedAddress"}, 
			description = "The IP address of a seed node")
	private String seedAddress = "165.22.129.19";
	
	@Option(names = {"-sPort", "--seedPort"}, 
			description = "The IP port of the seed node")
	private int seedPort = 3271;

	@Option(names = {"-s", "--seed"}, 
			description = "This node is a seed")
	private boolean seed = false;
	
	@Option(names = {"-m", "--mining"}, 
			description = "This node is mining")
	private boolean mining = false;

	@Option(names = {"-c", "--config"},
			description = "The full peer config file path. Eg: ./peer_config.txt")
	private String configFile = "./peer_config.txt";

	@Option(names = {"-b", "--blockfile"},
			description = "The full peer config file path. Eg: ./blockchain.txt")
	private String blockFile = "./blockchain.txt";
	
	@Option(names = {"--debug"},
			description = "Enable or disable seperate debugging console")
	private boolean debug = false;
	
	public StartHandler(Node node) {
		this.node = node;
	}
	
	@Override
	public Void call() throws Exception {
		if (debug) {
			Console.start();
			Console.log("Beginning node processes");
		}
		
		NetworkConfig config = new NetworkConfig();
		config.maxPeers = maxPeers;
		config.minPeers = minPeers;
		config.port = port;
		config.seed = seed;
		config.seedNode = new IPAddress(seedAddress, seedPort);
		config.mining = mining;
		node.PEER_CONFIG_FILE = configFile;
		node.BLOCKCHAIN_FILE = blockFile;
		node.initializeNetworkService(config);
		
		return null;
	}	
}
