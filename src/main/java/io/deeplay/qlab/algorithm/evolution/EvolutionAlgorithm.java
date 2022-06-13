package io.deeplay.qlab.algorithm.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class EvolutionAlgorithm {
    private final int lambda;                       // Размер популяции
    private final Function<List <List<String>>, Double> func;    // Функция приспособленности
    private final double chi;                       // Вероятность мутации
    private final int maxUnitAtLoc;
    private final int countLoc;
    private final ExecutorService service;
    private double costPopulation = 1;              // Переменная общая для класса, для удобства (оценка приспособленности
                                                    // популяции
    private Individual optInd;
    private double optCost = 0;

    private final int tournamentS;
    private final Random random = new Random();

    private static class Individual {
        public List <List<String>> locations;
        public List <String> emptyLoc;
        public double cost;

        public Individual(List <List<String>> locations, List <String> emptyLoc, double cost) {
            this.locations = new ArrayList<>(locations);
            this.emptyLoc = emptyLoc;
            this.cost = cost;
        }

        public Individual(Individual ind) {
            this(ind.locations, ind.emptyLoc, ind.cost);
        }

        public void updateCost(double cost) {
            this.cost = cost;
        }
    }

    private static int getPoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return k - 1;
    }

    public EvolutionAlgorithm(int lambda, int n, int tournamentS, double chi, int maxUnitAtLoc,
                              int countLoc, Function<List <List<String>>, Double> func) {
        this.lambda = lambda;
        this.maxUnitAtLoc = maxUnitAtLoc;
        this.countLoc = countLoc;
        this.func = func;
        this.chi = chi;
        service = Executors.newFixedThreadPool(8);
        this.tournamentS = tournamentS;
    }


    private void setOptInd(Individual ind) {
        if (ind.cost > optInd.cost) {
            optInd = ind;
        }
    }

    private void swapUnitAtLocs(List<String> loc1, List<String> loc2) {
        if (random.nextDouble() > 0.5) {
            List<String> b = loc1;
            loc1 = loc2;
            loc2 = b;
        }

        if (loc1.size() != 0) {
            String unit1 = loc1.remove(random.nextInt(loc1.size()));
            loc2.add(unit1);
            if (loc2.size() > maxUnitAtLoc) { //TODO: Заменить на (maxUnitAtLoc - EnemyUnitCountAtLoc)
                String unit2 = loc2.remove(random.nextInt(loc2.size()));
                loc1.add(unit2);
            }
        }

    }

    private void mutation(Individual individual) {
        int id = random.nextInt(countLoc+1);
        int id2 = random.nextInt(countLoc+1);

        if (id < id2) {
            int b = id2;
            id2 = id;
            id = b;
        }

        if (id == countLoc && id != id2) { // id попал на локацию с пулом юнитов (не игровую)
            swapUnitAtLocs(individual.emptyLoc, individual.locations.get(id2));
        }
        if (id != countLoc) {
            swapUnitAtLocs(individual.locations.get(id), individual.locations.get(id2));
        }

    }

    private List<Individual> tournamentSelectionAndMutation(List<Individual> population) {
        Random random = new Random();
        Queue<Individual> threadPopulation = new ConcurrentLinkedQueue<>();
        // Отсюда можно запустить распараллеливание
        CountDownLatch countDownLatch = new CountDownLatch(lambda);
        for (int i = 0; i < lambda; i++) {
            service.submit(() -> {
                // Селекция
                Individual newInd = population.get(random.nextInt(lambda));
                for (int j = 1; j < tournamentS; j++)  {
                    Individual buf = population.get(random.nextInt(lambda));
                    if (buf.cost > newInd.cost) {
                        newInd = buf;
                    }
                }
                newInd = new Individual(newInd);

                // Мутация по Пуассону
                int countMut = getPoisson(chi); // n * (chi/n) = chi
                for (int j = 0; j < countMut; j++) {
                    mutation(newInd);
                }
                newInd.updateCost(func.apply(newInd.locations));

                setOptInd(newInd);
                threadPopulation.add(newInd);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(threadPopulation);
    }

    private List<Individual> selectionAndMutation(List<Individual> population) {
        return tournamentSelectionAndMutation(population);
    }

    public List<List<String>> start(Integer iter, List<String> names) {
        // Строим случайным образом генотип
        List<Individual> population = new ArrayList<>();

        List<List<String>> randPop = new ArrayList<>();
        List<List<String>> randPopBuf = new ArrayList<>(); // Для полных локаций - буфер
        List<String> emptyLoc = new ArrayList<>(); // скидываем остальных юнитов сюда
        for (int i = 0; i < lambda; i++) {
            randPop.clear();
            randPopBuf.clear();
            for (int j = 0; j < countLoc; j++) {
                randPop.add(new ArrayList<>());
            }
            int countNotFullLoc = countLoc;
            for (String name : names) {
                if (randPop.size() != 0) {
                    List<String> buf = randPop.get(random.nextInt(countNotFullLoc));
                    if (buf.size() == countLoc) {
                        randPopBuf.add(buf);
                        countNotFullLoc--;
                        randPop.remove(buf);
                    } else {
                        buf.add(name);
                    }
                }
                else {
                    emptyLoc.add(name);
                }
            }
            randPop.addAll(randPopBuf);
            population.add(new Individual(randPop, emptyLoc, func.apply(randPop)));
        }

        // Строим новые популяции
        for(int i = 0; i < iter; i++) {
            population = selectionAndMutation(population);
        }
        return optInd.locations;
    }

}
