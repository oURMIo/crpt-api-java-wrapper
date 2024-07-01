import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        scheduler.scheduleAtFixedRate(this::resetRequestCount, 0, 1, timeUnit);
    }

    private void resetRequestCount() {
        lock.lock();
        try {
            requestCount.set(0);
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void createDocument(Document document, String signature) throws InterruptedException, IOException {
        lock.lock();
        try {
            while (requestCount.get() >= requestLimit) {
                notFull.await(1, timeUnit);
            }
            requestCount.incrementAndGet();
        } finally {
            lock.unlock();
        }

        String json = objectMapper.writeValueAsString(document);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://ismp.crpt.ru/api/v3/lk/documents/create")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Signature", signature)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    public static class Document {
        public Description description;
        public String doc_id;
        public String doc_status;
        public String doc_type = "LP_INTRODUCE_GOODS";
        public boolean importRequest;
        public String owner_inn;
        public String participant_inn;
        public String producer_inn;
        public LocalDate production_date;
        public String production_type;
        public Product[] products;
        public LocalDate reg_date;
        public String reg_number;

        public static class Description {
            public String participantInn;
        }

        public static class Product {
            public String certificate_document;
            public LocalDate certificate_document_date;
            public String certificate_document_number;
            public String owner_inn;
            public String producer_inn;
            public LocalDate production_date;
            public String tnved_code;
            public String uit_code;
            public String uitu_code;
        }
    }
}