package distributeblocks;

import java.security.*;
import distributeblocks.crypto.*;

/*
 * TransactionOut is used to keep track of a
 * specific exchange of funds to some 
 * receiving party.
 * 
 * This is one of the required pieces for a
 * full Transaction.
 */
public class TransactionOut {

	private String id; 				// ID of the transaction
	private PublicKey pk_Receiver; 	// Receiver of the coins
	private String id_Parent; 		// The id of the transaction this output was created in
	private float exchange; 		// Amount transfered / receiver owns

	public TransactionOut(PublicKey pk_Target, float amount, String id_Input) throws FailedToHashException{
		this.pk_Receiver = pk_Target;
		this.exchange = amount;
		this.id_Parent = id_Input;
		this.id = Crypto.calculateObjectHash(Crypto.keyToString(pk_Target)+Float.toString(amount)+ id_Input);
	}

	/*
	 * Check if a coin belongs to the given key
	 */
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == pk_Receiver);
	}
	
	/*
	 * Returns the exchange value of this transaction
	 */
	public float getExchange() {
		return exchange;
	}
	
	/*
	 * Returns the id of this TransactionOut
	 */
	public String getId() {
		return id;
	}

}