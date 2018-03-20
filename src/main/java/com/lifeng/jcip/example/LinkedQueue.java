package com.lifeng.jcip.example;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * LinkedQueue
 * <p/>
 * Insertion in the Michael-Scott nonblocking queue algorithm
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class LinkedQueue<E> {

    private static class Node<E> {

        final E item;

        final AtomicReference<Node<E>> next;

        public Node(E item, LinkedQueue.Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<LinkedQueue.Node<E>>(next);
        }
    }

    private final LinkedQueue.Node<E> dummy = new LinkedQueue.Node<E>(null, null);

    private final AtomicReference<LinkedQueue.Node<E>> head
            = new AtomicReference<LinkedQueue.Node<E>>(dummy);

    private final AtomicReference<LinkedQueue.Node<E>> tail
            = new AtomicReference<LinkedQueue.Node<E>>(dummy);

    public boolean put(E item) {
        LinkedQueue.Node<E> newNode = new LinkedQueue.Node<E>(item, null);

        while (true) {
            // 当前尾节点
            LinkedQueue.Node<E> curTail = tail.get();
            // 当前尾节点的下一个节点
            LinkedQueue.Node<E> tailNext = curTail.next.get();

            // if (尾节点 == 链表中的尾节点) 判断是否被并发修改了尾节点
            if (curTail == tail.get()) {
                //  尾节点的下一个节点不为 null
                if (tailNext != null) {
                    // Queue in intermediate state, advance tail
                    // 设置新的尾节点为 curTail 的下一下节点 tailNext ，因为尾节点已经发生了变化
                    tail.compareAndSet(curTail, tailNext);
                } else {
                    // In quiescent state, try inserting new node
                    if (curTail.next.compareAndSet(null, newNode)) {
                        // Insertion succeeded, try advancing tail
                        tail.compareAndSet(curTail, newNode);
                        return true;
                    }
                }
            }
        }
    }

}