package org.elasticsearch.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用 trie tree 用于匹配 mapping filter中的映射关系
 *
 * mapping ： aaa  aab  abc  构成 trie tree  结构
 * 对于 text ： abc , 223abrrrr
 * 实现判断  mapping 的某个值等于text完整文本， mapping 的某个值包含在text完整文本中。
 *
 */
public class TrieTree<T> {
    private final Node<T> root = new Node<>();

    public void addElements(T[] elements) {
        addElements(elements, 0);
    }

    public void addElements(List<T[][]> elements) {
        addElements(elements, 0);
    }

    /**
     * 向 trie 树中添加元素, 并且伴随着 value
     * @param elements
     */
    public void addElements(T[] elements, int value) {
        assert elements != null && elements.length > 0;
        Node<T> current = root;
        for (int i = 0; i< elements.length -1 ; i++) {
            current = current.addChild(elements[i], Hit.PREFIX);
        }
        final Node<T> tNode = current.addChild(elements[elements.length - 1], Hit.MATCH);
        tNode.getState().setValue(value);
    }

    /**
     * 向 trie 树中添加带有同义词的 phrase 信息， 对于其中的 元素说明： 对于 query中的term 来说，最复杂的是有同义词的情况
     * 对于没有 同义词的位置就是一个 字符 使用二维数组就是 elements[0][0]  , 对于有同义词的存在的情况时， 需要二维数组来表示m个同义词(每个同义词长度不一定相等)，elements[m][……]
     * @param elements
     * @param value
     */
    public void addElements(List<T[][]> elements, int value) {
        assert elements != null && elements.size() > 0;
        Node<T> current = root;
        for (int i = 0; i< elements.size() -1 ; i++) {
            assert elements.get(i).length > 0;
            if (elements.get(i).length == 1 && elements.get(i)[0].length == 1) {
                current = current.addChild(elements.get(i)[0][0], Hit.PREFIX);
            } else {
                // elements[m][……] 表示同义词的情况
                Node<T> commonEnd = new Node<>(Hit.PREFIX);
                for (int j = 0 ; j < elements.get(i).length; j++) {
                    T[] ts = elements.get(i)[j];
                    Node<T> tmp = current;
                    for (int k = 0; k < ts.length - 1; k++) {
                        tmp = tmp.addChild(ts[k], Hit.PREFIX);
                    }
                    tmp.addChild(ts[ts.length - 1], commonEnd);
                }
                current = commonEnd;
            }
        }
        // 处理最后的一段 term
        final T[][] lastElement = elements.get(elements.size() - 1);
        if (lastElement.length == 1 && lastElement[0].length == 1) {
            final Node<T> tNode = current.addChild(lastElement[0][0], Hit.MATCH);
            tNode.getState().setValue(value);
        } else {
            // elements[m][……] 表示同义词的情况
            Node<T> commonEnd = new Node<>(Hit.MATCH);
            for (int j = 0 ; j < lastElement.length; j++) {
                T[] ts = lastElement[j];
                Node<T> tmp = current;
                for (int k = 0; k < ts.length - 1; k++) {
                    tmp.addChild(ts[k], Hit.PREFIX);
                }
                tmp.addChild(ts[ts.length - 1], commonEnd);
            }
            final Node<T> tNode = commonEnd;
            tNode.getState().setValue(value);
        }
    }

    /**
     * 判断 给定的 element 是否等于 trie树中的某一个元素
     * @param elements
     * @return
     */
    public boolean isEquals(T[] elements) {
        Node<T> current = root;
        for (int i = 0; i < elements.length - 1; i++) {
            current = current.getChild(elements[i]);
            if (current == null) {
                return false;
            }
        }
        current = current.getChild(elements[elements.length - 1]);
        if (current != null && current.getState().isMatch()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 参数一般为一段长文本，
     * 判断这段长文本中是否有 trie 树中的元素
     * @param elements
     * @return
     */
    public boolean isContains(T[] elements) {
        assert elements != null && elements.length > 0;
        for (int i = 0; i < elements.length; i++ ) {
            if (isContains(elements, i)){
                return true;
            }
        }
        return false;
    }

    public Integer[] getMatchValues(T[] elements, int beginIndex) {
        assert beginIndex < elements.length;
        List<Integer> result = new ArrayList<>();
        Node<T> current = root;
        for (int i = beginIndex; i < elements.length; i++) {
            current = current.getChild(elements[i]);
            if (current == null || current.getState().isUnmatch()) {
                break;
            }else if (current.getState().isMatch()) {
                result.add(current.getState().getValue());
            }
        }
        return result.toArray(new Integer[0]);
    }
    public int getMaxMatchLength(T[] elements, int beginIndex) {
        assert beginIndex < elements.length;
        Node<T> current = root;
        int count = 0;
        int tmpCount = 0;
        for (int i = beginIndex; i < elements.length; i++) {
            current = current.getChild(elements[i]);
            if (current == null || current.getState().isUnmatch()) {
                break;
            }else if (current.getState().isMatch()) {
                tmpCount++;
                count = tmpCount;
            } else {
                // prefix 的情况
                tmpCount++;
            }
        }
        return count;
    }


    /**
     * trie 树中的某个值是否在 长文本 elements 的 beginIndex 位置出现。
     * @param elements
     * @param beginIndex
     * @return
     */
    private boolean isContains(T[] elements, int beginIndex) {
        final Integer[] matchValue = getMatchValues(elements, beginIndex);
        return matchValue.length != 0;
    }

    /**
     *
     * @param elements
     * @return
     */
    public Hit match(T[] elements) {
        return match(elements, 0, elements.length);
    }

    /**
     * 判断 elements[beginIndex:beginIndex+length] 的匹配情况
     * @param elements
     * @param beginIndex
     * @param length
     * @return
     */
    public Hit match(T[] elements, int beginIndex, int length) {
        assert beginIndex + length <= elements.length;
        Node<T> current = root;
        for (int i = beginIndex; i < beginIndex + length; i++) {
            current = current.getChild(elements[i]);
            if (current == null) {
                return new Hit(Hit.UNMATCH);
            }
        }
        return current.getState();
    }

}

class Node<T> {
    // 从root 到本节点为一个匹配的值
    private Hit state;
    private Map<T, Node<T>> children; // 维护着当前节点上和下层的映射关系

    public Node() {
        this.state = new Hit();
    }
    public Node(int state) {
        this.state = new Hit(state);
    }

    Node<T> addChild(T t, int matchState) {
        if (children == null) {
            children = new HashMap<>();
        }
        Node<T> current;
        if (!children.containsKey(t)) {
            current = new Node<>(matchState);
            children.put(t, current);
        } else {
            current = children.get(t);
            current.state.setHitState(matchState);
        }
        return current;
    }

    Node<T> addChild(T t, Node nextNode) {
        if (children == null) {
            children = new HashMap<>();
        }
        Node<T> current;
        if (!children.containsKey(t)) {
            current = nextNode;
            children.put(t, nextNode);
        } else {
            current = children.get(t);
            current.state.setHitState(nextNode.state.getHitState());
        }
        return current;
    }

    Node<T> getChild(T t) {
        if (children == null) {
            return null;
        }
        return children.getOrDefault(t, null);
    }

    Hit getState() {
        return this.state;
    }

    boolean isEnd() {
        return children == null || children.size() == 0;
    }

}
