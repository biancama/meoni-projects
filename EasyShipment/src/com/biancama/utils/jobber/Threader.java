package com.biancama.utils.jobber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.biancama.http.download.Broadcaster;
import com.biancama.http.download.DownloadChunk;

/**
 * Dieser Klasse kann man beliebig viele Threads hinzufügen. mit der
 * startAndWait() kann anschliesend gewartet werden bis alle beendet sind
 * 
 * @author coalado
 * 
 */
public class Threader {

    public static void main(String[] args) throws Exception {

        Threader th = new Threader();

        for (int i = 0; i < 1000; i++) {
            th.add(new BiancaRunnable() {
                public void go() throws Exception {
                    System.out.println("DA");

                }

            });

        }

        th.startAndWait();

        System.out.println("ALLES OK");

    }

    private final ArrayList<Worker> workerlist;
    private Integer returnedWorker = 0;
    private boolean waitFlag = false;
    private final Broadcaster<WorkerListener> broadcaster;
    private boolean hasDied = false;
    private boolean hasStarted = false;

    public boolean isHasStarted() {
        return hasStarted;
    }

    public Threader() {
        broadcaster = new Broadcaster<WorkerListener>();
        workerlist = new ArrayList<Worker>();
    }

    public Broadcaster<WorkerListener> getBroadcaster() {
        return broadcaster;
    }

    public void add(BiancaRunnable runnable) {
        if (this.hasDied) { throw new IllegalStateException("Threader already has died"); }
        Worker worker = new Worker(runnable);
        synchronized (workerlist) {
            workerlist.add(worker);
        }
        if (this.hasStarted) {
            worker.start();
        }
    }

    public boolean isHasDied() {
        return hasDied;
    }

    public synchronized void interrupt() {
        for (Worker w : workerlist) {
            if (w.isRunnableAlive()) {
                w.interrupt();
            }
        }
    }

    private synchronized void onWorkerFinished(Worker w) {
        returnedWorker++;
        for (int i = 0; i < broadcaster.size(); i++) {
            broadcaster.get(i).onThreadFinished(this, w.getRunnable());
        }
        if (returnedWorker == workerlist.size()) {
            this.waitFlag = false;
            this.notify();
        }
    }

    public void startWorkers() {
        this.hasStarted = true;
        for (Worker w : workerlist) {
            w.start();
        }

        waitFlag = true;
    }

    public void waitOnWorkers() {
        synchronized (this) {
            while (waitFlag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    for (Worker w : workerlist) {
                        w.interrupt();
                    }

                    return;
                }
            }
        }
        this.hasDied = true;
    }

    public void startAndWait() {
        this.hasStarted = true;
        for (Worker w : workerlist) {
            w.start();
        }

        waitFlag = true;
        synchronized (this) {
            while (waitFlag) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    for (Worker w : workerlist) {
                        w.interrupt();
                    }

                    return;
                }
            }
        }
        this.hasDied = true;
    }

    public class Worker extends Thread {

        private final BiancaRunnable runnable;
        private boolean runnableAlive = false;

        public Worker(BiancaRunnable runnable) {

            this.runnable = runnable;

        }

        @Override
        public String toString() {
            return "Worker for " + runnable;
        }

        public BiancaRunnable getRunnable() {
            return runnable;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < broadcaster.size(); i++) {
                    broadcaster.get(i).onThreadStarts(Threader.this, getRunnable());
                }
                this.runnableAlive = true;
                runnable.go();
            } catch (Exception e) {
                for (int i = 0; i < broadcaster.size(); i++) {
                    broadcaster.get(i).onThreadException(Threader.this, getRunnable(), e);
                }

                // JDLogger.exception(e);
            } finally {
                this.runnableAlive = false;
            }
            onWorkerFinished(this);
        }

        public boolean isRunnableAlive() {
            return runnableAlive;
        }

    }

    public abstract class WorkerListener {

        public abstract void onThreadFinished(Threader th, BiancaRunnable runnable);

        public abstract void onThreadStarts(Threader threader, BiancaRunnable runnable);

        public abstract void onThreadException(Threader th, BiancaRunnable job, Exception e);

    }

    public int size() {

        return workerlist.size();
    }

    public BiancaRunnable get(int i) {

        return workerlist.get(i).getRunnable();
    }

    public void interrupt(DownloadChunk slowest) {
        for (Worker w : workerlist) {
            if (w.getRunnable() == slowest) {
                System.err.println("Interruot:, " + w + " - " + w.getRunnable() + "-" + slowest);
                w.interrupt();
                return;

            }
        }

    }

    public void sort(Comparator<Worker> comparator) {
        Collections.sort(workerlist, comparator);
    }

    public ArrayList<BiancaRunnable> getAlive() {
        ArrayList<BiancaRunnable> list = new ArrayList<BiancaRunnable>();
        for (Worker w : workerlist) {
            if (w.isRunnableAlive()) {
                list.add(w.getRunnable());
            }
        }
        return list;
    }

}
