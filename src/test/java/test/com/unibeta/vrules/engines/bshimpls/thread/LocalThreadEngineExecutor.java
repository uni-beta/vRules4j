
////         for (int i = 0; i < size; i++) {
////         FutureResult futureResult = new FutureResult();
////         String entityId = objs.getClass().getSimpleName();
////         executor.execute(futureResult
////         .setter(new LocalEngineThread(objs, entityId)));
////                    
////         futureResult.get();
////         futureResults[i] = futureResult;
////                    
////         }
//
//        for (int i = 0; i < size; i++) {
//
//             //String[] strs = (String[]) futureResults[i].get();
//            String[] strs = execute(objs[i]);
//            CommonUtils.converArrayToList(strs, list);
//        }
//
//        return (String[]) list.toArray(new String[] {});
//    }
//
//    public static String[] execute(Object obj) throws Exception {
//
//        String entityId = generateClassName(obj);
//        return execute(obj, entityId);
//    }
//
//    private static String generateClassName(Object object) {
//
//        String name = new String(object.getClass().getName());
//        int len = name.lastIndexOf(".");
//        name = name.substring(len + 1);
//
//        return name;
//    }
//
//    private static void init() {
//
//        if (null == executor) {
//            executor = new PooledExecutor(new Slot(),MAX_POOL_SIZE);
//         //   executor = new DirectExecutor();
//        }
//    }
//}
