
//
//    public LocalEngineThread(Object object) {
//
//        this.object = object;
//        this.entityId = object.getClass().getSimpleName();
//    }
//
//    public LocalEngineThread(Object object, String entityId) {
//
//        this.object = object;
//        this.entityId = entityId;
//    }
//
//    public Object call() throws Exception {
//
//        return CoreEngine.validate(this.object, this.entityId);
//    }
//
//    public void run() {
//
//       while (!finished.booleanValue()) {
//            try {
//                results = CoreEngine.validate(object, entityId);
//                finished = Boolean.TRUE;
//
//            } catch (Exception e) {
//                exception = e;
//
//                finished = Boolean.TRUE;
//                // finished.notifyAll();
//
//            }
//        }
//
//    }
//
//    /**
//     * Fetchs the execute results. If current thread is sill running, wait it
//     * until stopping.
//     * 
//     * @return execute results.
//     * @throws Exception
//     */
//    public String[] getResult() throws Exception {
//
//        while (!finished.booleanValue()) {
//            // finished.wait();
//        }
//
//        return results;
//    }
//
//    /**
//     * check whether the current thread has stoped.
//     * 
//     * @return true if stoped, otherwise wait until it is finished.
//     * @throws Exception
//     */
//    public boolean isFinished() throws Exception {
//
//        while (!finished.booleanValue()) {
//            // finished.wait();
//        }
//
//        return true;
//    }

//}
