## 介绍
本plugin引入一对分词器 max_word_index max_word_search 分词器，分别应用于 index阶段和search阶段；</br>
下面通过一个例子来说明使用场景：
```json
PUT test_index
{
  "mappings": {
    "properties": {
      "content":{
        "type": "text",
        "analyzer": "max_word_index",
        "search_analyzer": "max_word_search"
      }
    }
  }
}

POST test_index/_doc/1
{
  "content": "如果需要覆盖原来的配置"
}

GET test_index/_search
{
  "query": {
    "match_phrase": {
      "content": {
        "query": "要覆盖"
      }
    }
  }
}
```