package io.deeplay.qlab.algorithm.evolution;

import io.deeplay.qlab.algorithm.eval.IEvaluator;
import io.deeplay.qlab.parser.models.Unit;
import io.deeplay.qlab.parser.models.input.EnemyLocation;
import io.deeplay.qlab.parser.models.output.UnitWithLocation;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EvolutionAlgorithm {
    private final int lambda;                       // Размер популяции
    private final IEvaluator func;    // Функция приспособленности
    private final double chi;                       // Вероятность мутации
    private final int maxUnitAtLoc;
    private final int countLoc;
    private final List<EnemyLocation> locations;
    private final ExecutorService service;
    private Individual optInd;

    private final int tournamentS;
    private final Random random = new Random();

    private static class Individual {
        public List <List<Unit>> locations;
        public List <Unit> emptyLoc;
        public double cost;

        public Individual(List <List<Unit>> locations, List <Unit> emptyLoc, double cost) {
            this.locations = new ArrayList<>();
            for (List<Unit> loc: locations) {
                this.locations.add(new ArrayList<>(loc));
            }
            this.emptyLoc = new ArrayList<>(emptyLoc);
            this.cost = cost;
        }

        public Individual(Individual ind) {
            this(ind.locations, ind.emptyLoc, ind.cost);
        }

        public void updateCost(double cost) {
            this.cost = cost;
        }
    }

    private double getCost(Individual individual) {
        Set<UnitWithLocation> set = new HashSet<>();
        int k = 0;
        for (List<Unit> item: individual.locations) {
            int i = 0;
            EnemyLocation loc = locations.get(k);
            Set<Integer> index = loc.getOpponentUnits().stream().map(Unit::getLocatePosition)
                    .collect(Collectors.toCollection(TreeSet::new));    // позиции врагов
            for (Unit unit: item) {
                while (index.contains(i)) { i++; } // пропускаем позиции врагов

                if (i >= maxUnitAtLoc) {
                    break;
                }

                set.add(new UnitWithLocation(unit.getName(), unit.getSourceGoldCount(), i, loc));
                i++;
            }
            k++;
        }
        return func.evaluateGoldProfit(set);
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

    private Set<UnitWithLocation> placeUnit(Individual ind) {
        Set<UnitWithLocation> set = new HashSet<>();
        int k = 0;
        for (List<Unit> item: ind.locations) {
            EnemyLocation loc = locations.get(k);
            Set<Integer> index = loc.getOpponentUnits().stream().map(Unit::getLocatePosition)
                    .collect(Collectors.toCollection(TreeSet::new));    // позиции врагов

            int i = 0;
            for (Unit unit: item) {
                while (index.contains(i)) { i++; } // пропускаем позиции врагов

                if (i >= maxUnitAtLoc) {
                    break;
                }

                set.add(new UnitWithLocation(unit.getName(), unit.getSourceGoldCount(), i, locations.get(k)));
                i++;

            }
        }
        return set;
    }

    /**
     * @param lambda        - количество особей в популяции
     * @param tournamentS   - количество особей в турнире (естественный отбор сильнейшей среди S особей)
     * @param chi           - константа вероятности мутации (обычно ставят 1) (для Пуассоновского распределения)
     * @param func          - IEvaluator
     * @param locations     - локации
     */
    public EvolutionAlgorithm(int lambda, int tournamentS, double chi,
                              IEvaluator func, List<EnemyLocation> locations) {
        this.lambda = lambda;
        this.countLoc = locations.size();
        this.maxUnitAtLoc = (countLoc > 0)? locations.get(0).getMaxPositionsQuantity(): 0;
        this.func = func;
        this.chi = chi;
        service = Executors.newFixedThreadPool(8);
        this.tournamentS = tournamentS;
        this.locations = locations;
    }

    private void setOptInd(Individual ind) {
        if (ind.cost > optInd.cost) {
            optInd = ind;
        }
    }

    private void swapUnitAtLocs(List<Unit> loc1, List<Unit> loc2) {
        if (random.nextDouble() > 0.5) {
            List<Unit> b = loc1;
            loc1 = loc2;
            loc2 = b;
        }

        if (loc1.size() != 0) {
            Unit unit1 = loc1.remove(random.nextInt(loc1.size()));
            loc2.add(unit1);
            if (loc2.size() > maxUnitAtLoc) {
                Unit unit2 = loc2.remove(random.nextInt(loc2.size()));
                loc1.add(unit2);
            }
        }

    }

    private void mutation(Individual individual) {
        int id = random.nextInt(countLoc + 1);
        int id2 = random.nextInt(countLoc + 1);

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
                for (int j = 1; j < tournamentS; j++) {
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
                newInd.updateCost(getCost(newInd));
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

    public Set<UnitWithLocation> start(List<Unit> names) {
        // Строим случайным образом генотип
        List<Individual> population = new ArrayList<>();

        List<List<Unit>> randPop = new ArrayList<>();
        List<List<Unit>> randPopBuf = new ArrayList<>(); // Для полных локаций - буфер
        List<Unit> emptyLoc = new ArrayList<>(); // скидываем остальных юнитов сюда
        for (int i = 0; i < lambda; i++) {
            randPop.clear();
            randPopBuf.clear();
            emptyLoc.clear();
            for (int j = 0; j < countLoc; j++) {
                randPop.add(new ArrayList<>());
            }
            int countNotFullLoc = countLoc;
            for (Unit name : names) {
                if (randPop.size() != 0) {
                    List<Unit> buf = randPop.get(random.nextInt(countNotFullLoc));
                    if (buf.size() == countLoc) {
                        randPopBuf.add(buf);
                        countNotFullLoc--;
                        randPop.remove(buf);
                    } else {
                        buf.add(name);
                    }
                } else {
                    emptyLoc.add(name);
                }
            }
            randPop.addAll(randPopBuf);
            Individual ind = new Individual(randPop, emptyLoc, 0);
            ind.updateCost(getCost(ind));
            if (optInd == null || ind.cost > optInd.cost) {
                optInd = ind;
            }
            population.add(ind);
        }

        // Строим новые популяции
        long startTime = System.currentTimeMillis();
        long sumTime = 0;
        for(long curTime = 0, i = 1;
                        curTime + sumTime/(double)i < 5000. ;
                        curTime = System.currentTimeMillis() - startTime, i++,
                        sumTime += curTime)
        {
            population = selectionAndMutation(population);
        }
        return placeUnit(optInd);
    }

    private static class Individual {
        public List<List<String>> locations;
        public List<String> emptyLoc;
        public double cost;

        public Individual(List<List<String>> locations, List<String> emptyLoc, double cost) {
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

}
