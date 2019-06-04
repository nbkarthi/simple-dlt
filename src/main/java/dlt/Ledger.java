package dlt;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import dlt.common.Utils;
import dlt.model.PaymentModel;
import dlt.model.Payments;

@Singleton
public class Ledger {
	private final String blockPath = System.getProperty("dlt.path");

	public String lastHash() {
			final Optional<String> fileNameOpt = lastBlockName();
			if (fileNameOpt.isPresent()) {
				final PaymentBlock pb = PaymentBlockOf(fileNameOpt.get());
				return pb.getHash();
			}
		return "Genesis";
	}

	private PaymentBlock PaymentBlockOf(final String fileName){
		try {
			final String content = new String(Files.readAllBytes(Paths.get(blockPath, fileName)));
			return Utils.fromJSON(content, PaymentBlock.class);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Block Read Operation Failed..");
		}
	}

	private List<Long> blockNames(){
		try {
			return Files.list(Paths.get(blockPath))
					.map(each -> each.getFileName().toString())
					.mapToLong(Long::parseLong)
					.boxed()
					.sorted()
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	
	private Optional<String> lastBlockName(){
		final List<Long> blockNames = blockNames();
		
		if(blockNames.size() > 0){
			Long max = blockNames.get(blockNames.size()-1);
			return Optional.of(String.valueOf(max));
		}
		return Optional.empty();
	}
	
	public void createBlock(final PaymentBlock pb) {
		try {
			final String json = Utils.toJSON(pb);
			Files.write(Paths.get(blockPath, pb.blockNo), json.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String blocks() {

		List<PaymentModel> list = new ArrayList<>();
			
		PaymentBlock previousBlock=null;
		blockNames().stream()
			.map(String::valueOf)
			.forEach(fileName -> {
				final PaymentBlock pb = PaymentBlockOf(fileName);
				int status = status(previousBlock, pb);
				
				final PaymentModel pm = pb.getPayment().model()
				.withBlockDetails(fileName, status);
					
				list.add(pm);
			});
		
		return Utils.toJSON(new Payments(list));
	}

	private int status(final PaymentBlock previousBlock, final PaymentBlock pb) {
		if (!pb.getHash().equals(pb.calculateHash())) {
			return 1;
		} else if (previousBlock != null && !previousBlock.getHash().equals(pb.getPreviousHash())) {
			return 2;
		}
		return 0;
	}

}
