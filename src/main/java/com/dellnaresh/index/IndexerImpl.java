package com.dellnaresh.index;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Created by nmiriyal on 20/02/2017.
 */
@Component
public class IndexerImpl implements Indexer {
    @Value("${app.coles-index.name}")
    private String indexName;
    @Value("${app.coles-index.type}")
    private String indexType;
    private Logger logger = LoggerFactory.getLogger(IndexerImpl.class);

    @Autowired
    private ElasticsearchTemplate template;
    @Override
    public void indexMesssages(List<JSONObject> messages){
        List<IndexQuery> indexQueries=new ArrayList<>();
    messages.stream().forEach(message->{
        String id = message.getString("p");

        indexQueries.add(logIndex(message.toString(), id));

    });
        indexJson(indexQueries);
    }
    private void indexJson(List<IndexQuery> jsons) {
        if (nonNull(jsons)) {

            template.bulkIndex(jsons);
            logger.info("Successfully indexed messages");
        }
    }
    private IndexQuery logIndex(String json,String id) {

        logger.info("Indexing message with id '{}'", id);
        return new IndexQueryBuilder().withSource(json)
                .withIndexName(indexName).withId(id)
                .withType(indexType).build();
    }
}
