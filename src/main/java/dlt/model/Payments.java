package dlt.model;
import java.util.ArrayList;
import java.util.List;

public class Payments {
	List<PaymentModel> data = new ArrayList<>();
	
	public Payments(List<PaymentModel> data){
		this.data = data;
	}
}
