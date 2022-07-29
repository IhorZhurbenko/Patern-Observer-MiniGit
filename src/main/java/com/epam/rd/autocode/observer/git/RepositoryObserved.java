package com.epam.rd.autocode.observer.git;

import java.util.*;

public class RepositoryObserved implements Repository {
    List<Branch> branchList;
    Map<Branch, List<Commit>> commits;
    List<WebHook> webHookList;

    public RepositoryObserved() {
        this.branchList = new ArrayList<>();
        this.commits = new HashMap<>();
        webHookList = new ArrayList<>();
        commits.put(new Branch("main"), new ArrayList<>());
        branchList.add(new Branch("main"));
    }

    @Override
    public Branch getBranch(String name) {
        for (Branch branch : branchList) {
            if (branch.toString().equals(name)) {
                return branch;
            }
        }
        return null;
    }

    @Override
    public Branch newBranch(Branch sourceBranch, String name) {
        int exist = 0;
        for (Branch b : branchList) {
            if (b.toString().equals(name)) {
                throw new IllegalArgumentException();
            }
        }
        for (Branch b : branchList) {
            if (b.equals(sourceBranch)) {
                ++exist;
            }
        }
        if (exist == 0) {
            throw new IllegalArgumentException();
        }
        Branch branch = new Branch(name);
        branchList.add(branch);
        List<Commit> sourceCommits = commits.get(sourceBranch);
        commits.put(branch, new ArrayList<>(sourceCommits));
        return branch;
    }

    @Override
    public Commit commit(Branch branch, String author, String[] changes) {
        Commit commit = new Commit(author, changes);
        List<Commit> commitList = commits.get(branch);
        commitList.add(commit);
        for (WebHook w : webHookList) {
            if (w.type() == Event.Type.COMMIT && w.branch().equals(branch.toString())) {
                w.onEvent(new Event(Event.Type.COMMIT, branch, List.of(commit)));
            }
        }
        return commit;
    }

    @Override
    public void merge(Branch sourceBranch, Branch targetBranch) {
        List<Commit> sc = commits.get(sourceBranch);
        List<Commit> tc = commits.get(targetBranch);
        List<Commit> mc = new ArrayList<>(sc);
        mc.removeAll(tc);
        boolean same = sc.equals(tc);
        if (!same) {
            tc.addAll(mc);
            for (WebHook w : webHookList) {
                if (w.type() == Event.Type.MERGE && w.branch().equals(targetBranch.toString())) {
                    w.onEvent(new Event(Event.Type.MERGE, targetBranch, mc));
                }
            }
        }
    }

    @Override
    public void addWebHook(WebHook webHook) {
        webHookList.add(webHook);
    }
}