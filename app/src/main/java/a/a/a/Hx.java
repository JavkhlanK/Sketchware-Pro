package a.a.a;

import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.EventBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.ViewBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mod.hey.studios.project.ProjectSettings;
import mod.hilal.saif.components.ComponentExtraCode;
import mod.pranav.viewbinding.ViewBindingBuilder;

public class Hx {

    private final ProjectFileBean projectFileBean;
    private final jq jq;
    private final ArrayList<Event> viewEvents = new ArrayList<>();
    private final ArrayList<Event> componentEvents = new ArrayList<>();
    private final ArrayList<ActivityEvent> activityEvents = new ArrayList<>();
    private final ArrayList<Event> authEvents = new ArrayList<>();
    private final ArrayList<Event> drawerViewEvents = new ArrayList<>();
    private final ArrayList<ComponentCallback> callbackEvents = new ArrayList<>();
    private final ArrayList<String> imports = new ArrayList<>();
    private final HashMap<String, String> activityLifecycleEvents = new HashMap<>();
    public String k = "";
    public String l = "";
    private final Boolean isViewBindingEnabled;

    public Hx(jq jq, ProjectFileBean projectFileBean, eC eC) {
        this.jq = jq;
        this.projectFileBean = projectFileBean;

        ProjectSettings projectSettings = new ProjectSettings(jq.sc_id);
        isViewBindingEnabled = projectSettings.getValue(ProjectSettings.SETTING_ENABLE_VIEWBINDING, "false").equals("true");

        ArrayList<ViewBean> views = new ArrayList<>(eC.d(projectFileBean.getXmlName()));
        if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_FAB)) {
            ViewBean fab = eC.h(projectFileBean.getXmlName());
            views.add(fab);
        }
        for (ViewBean view : views) {
            viewEvents.add(new Event(this, isViewBindingEnabled ? "binding." + ViewBindingBuilder.generateParameterFromId(view.id) : view.id, view.getClassInfo(), isViewBindingEnabled));
        }

        ArrayList<ComponentBean> components = eC.e(projectFileBean.getJavaName());
        for (ComponentBean componentBean : components) {
            int type = componentBean.type;
            if (type == ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH || type == ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD) {
                authEvents.add(new Event(this, componentBean.componentId, componentBean.getClassInfo(), isViewBindingEnabled));
            } else {
                componentEvents.add(new Event(this, componentBean.componentId, componentBean.getClassInfo(), isViewBindingEnabled));
            }
        }

        if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER)) {
            ArrayList<ViewBean> drawerViews = eC.d(projectFileBean.getDrawerXmlName());
            for (ViewBean view : drawerViews) {
                drawerViewEvents.add(new Event(this, isViewBindingEnabled ? "binding.drawer." + ViewBindingBuilder.generateParameterFromId(view.id) : "_drawer_" + view.id, view.getClassInfo(), isViewBindingEnabled));
            }
        }

        a(eC.g(projectFileBean.getJavaName()), eC.b(projectFileBean.getJavaName()));
    }

    public String getOnActivityResultSwitchCases() {
        StringBuilder sb = new StringBuilder(4096);
        for (ComponentCallback value : callbackEvents) {
            String code = value.getCode();
            if (sb.length() > 0 && !code.isEmpty()) {
                sb.append(Jx.EOL);
                sb.append(Jx.EOL);
            }
            sb.append(code);
        }
        return sb.toString();
    }

    private void addCallbackEvents(int targetType, String targetId, String eventName, String eventLogic) {
        boolean hasOnSuccessLogic = eventName.equals("onPictureTaken") || eventName.equals("onAccountPicker") || eventName.equals("onFilesPicked");
        boolean hasOnCancelledLogic = eventName.equals("onPictureTakenCancel") || eventName.equals("onFilesPickedCancel") || eventName.equals("onAccountPickerCancelled");

        boolean alreadyRegistered = false;
        for (ComponentCallback next : callbackEvents) {
            if (next.componentName.equals(targetId)) {
                if (hasOnSuccessLogic) {
                    next.setOnSuccessLogic(eventLogic);
                    return;
                } else if (hasOnCancelledLogic) {
                    next.setOnCancelledLogic(eventLogic);
                    return;
                } else {
                    alreadyRegistered = true;
                }
            }
        }
        if (!alreadyRegistered) {
            ComponentCallback event = new ComponentCallback(targetType, targetId);
            if (hasOnSuccessLogic) {
                event.setOnSuccessLogic(eventLogic);
            } else if (hasOnCancelledLogic) {
                event.setOnCancelledLogic(eventLogic);
            }
            callbackEvents.add(event);
        }
    }

    private void addActivityEvent(String eventName, String eventLogic) {
        ActivityEvent target = null;

        for (ActivityEvent next : activityEvents) {
            if (next.name.equals(eventName)) {
                target = next;
                break;
            }
        }

        if (target == null) {
            target = new ActivityEvent(eventName);
            activityEvents.add(target);
        }
        target.setLogic(eventLogic);
    }

    public void addLifecycleEvent(String eventName, String viewType, String viewId) {
        if (!activityLifecycleEvents.containsKey(eventName)) {
            activityLifecycleEvents.put(eventName, Lx.getDefaultActivityLifecycleCode(eventName, viewType, isViewBindingEnabled ? ViewBindingBuilder.generateParameterFromId(viewId) : viewId));
        }
    }

    private void a(ArrayList<EventBean> events, HashMap<String, ArrayList<BlockBean>> logicBlocks) {
        for (EventBean eventBean : events) {
            ArrayList<BlockBean> eventLogicBlocks = logicBlocks.get(eventBean.targetId + "_" + eventBean.eventName);
            String eventLogic = (eventLogicBlocks == null || eventLogicBlocks.isEmpty()) ? "" :
                    new Fx(projectFileBean.getActivityName(), jq, eventLogicBlocks, isViewBindingEnabled).a();

            switch (eventBean.eventType) {
                case EventBean.EVENT_TYPE_VIEW:
                    addViewListeners(eventBean.targetId, eventBean.eventName, eventLogic);
                    break;

                case EventBean.EVENT_TYPE_COMPONENT:
                    switch (eventBean.targetType) {
                        case ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH:
                        case ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD:
                            addCallbackEvents(eventBean.targetId, eventBean.eventName, eventLogic);
                            break;

                        case ComponentBean.COMPONENT_TYPE_CAMERA:
                        case ComponentBean.COMPONENT_TYPE_FILE_PICKER:
                        case ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_GOOGLE_LOGIN:
                            addCallbackEvents(eventBean.targetType, eventBean.targetId, eventBean.eventName, eventLogic);
                            break;

                        default:
                            addViewImports(eventBean.targetId, eventBean.eventName, eventLogic);
                    }
                    break;

                case EventBean.EVENT_TYPE_ACTIVITY:
                    addActivityEvent(eventBean.eventName, eventLogic);
                    break;

                case EventBean.EVENT_TYPE_DRAWER_VIEW:
                    addDrawerEvents(eventBean.targetId, eventBean.eventName, eventLogic);
                    break;

                default:
            }
        }
    }

    public String generateActivityLifecycleEventCode() {
        StringBuilder sb = new StringBuilder(4096);
        for (Map.Entry<String, String> event : activityLifecycleEvents.entrySet()) {
            String name = event.getKey();
            String logic = event.getValue();

            boolean found = false;
            for (ActivityEvent next : activityEvents) {
                if (next.name.equals(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ActivityEvent a = new ActivityEvent(name);
                a.setLogic(logic);
                activityEvents.add(a);
            }
        }

        for (ActivityEvent value : activityEvents) {
            String code = value.getCode();
            if (sb.length() > 0 && !code.isEmpty()) {
                sb.append(Jx.EOL);
                sb.append(Jx.EOL);
            }
            sb.append(code);
        }
        return sb.toString();
    }

    private void addViewImports(String targetId, String eventName, String eventLogic) {
        for (Event next : componentEvents) {
            if (next.id.equals(targetId)) {
                next.addEvent(targetId, eventName, eventLogic);
                return;
            }
        }
    }

    public String generateComponentEvents() {
        StringBuilder sb = new StringBuilder(4096);
        ComponentExtraCode componentExtraCode = new ComponentExtraCode(this, sb);
        for (Event next : componentEvents) {
            componentExtraCode.s(next.generateEvent());
        }
        return sb.toString();
    }

    private void addDrawerEvents(String targetId, String eventName, String eventLogic) {
        for (Event next : drawerViewEvents) {
            if (next.id.equals(isViewBindingEnabled ? "binding.drawer." + ViewBindingBuilder.generateParameterFromId(targetId) : "_drawer_" + targetId)) {
                next.addEvent(targetId, eventName, eventLogic);
                return;
            }
        }
    }

    public String generateDrawerEvents() {
        StringBuilder sb = new StringBuilder(4096);
        for (Event next : drawerViewEvents) {
            String a2 = next.generateEvent();
            if (sb.length() > 0 && !a2.isEmpty()) {
                sb.append(Jx.EOL);
                sb.append(Jx.EOL);
            }
            sb.append(a2);
        }
        return sb.toString();
    }

    private void addCallbackEvents(String targetId, String eventName, String eventLogic) {
        for (Event next : authEvents) {
            if (next.id.equals(targetId)) {
                next.addEvent(targetId, eventName, eventLogic);
                return;
            }
        }
    }

    /**
     * @return {@link Hx#imports}
     */
    public ArrayList<String> getImports() {
        return imports;
    }

    private void addViewListeners(String targetId, String eventName, String eventLogic) {
        for (Event next : viewEvents) {
            if (next.id.equals(isViewBindingEnabled ? "binding." + ViewBindingBuilder.generateParameterFromId(targetId) : targetId)) {
                next.addEvent(targetId, eventName, eventLogic);
                return;
            }
        }
    }

    public String generateAuthEvents() {
        StringBuilder sb = new StringBuilder(4096);
        for (Event next : authEvents) {
            String event = next.generateEvent();
            if (sb.length() > 0 && !event.isEmpty()) {
                sb.append(Jx.EOL);
                sb.append(Jx.EOL);
            }
            sb.append(event);
        }
        return sb.toString();
    }

    public String generateViewEvents() {
        StringBuilder sb = new StringBuilder(4096);
        for (Event value : viewEvents) {
            String event = value.generateEvent();
            if (sb.length() > 0 && !event.isEmpty()) {
                sb.append(Jx.EOL);
                sb.append(Jx.EOL);
            }
            sb.append(event);
        }
        return sb.toString();
    }

    private static class ActivityEvent {

        private final String name;
        private String logic = "";
        private String targetId = "";

        private ActivityEvent(String name) {
            this.name = name;
        }

        private String getCode() {
            return Lx.getEventCode(targetId, name, logic);
        }

        private void setLogic(String logic) {
            this.logic = logic;
        }

        private void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    private static class ComponentCallback {

        private final int componentId;
        private final String componentName;
        private String onSuccessCode = "";
        private String onCancelledCode = "";

        private ComponentCallback(int componentId, String componentName) {
            this.componentId = componentId;
            this.componentName = componentName;
        }

        private void setOnCancelledLogic(String onCancelledLogic) {
            onCancelledCode = onCancelledLogic;
        }

        private void setOnSuccessLogic(String onSuccessLogic) {
            onSuccessCode = onSuccessLogic;
        }

        private String getCode() {
            return Lx.getOnActivityResultCode(componentId, componentName, onSuccessCode, onCancelledCode);
        }
    }

    private static class Event {
        private final Hx hx;
        private final String id;
        private final ArrayList<ComponentEvents> listeners = new ArrayList<>();
        private final boolean isViewBindingEnabled;
        private final Gx classInfo; // to be used in generateEvent()

        private Event(Hx hx, String id, Gx classInfo, boolean isViewBindingEnabled) {
            this.hx = hx;
            this.id = id;
            this.isViewBindingEnabled = isViewBindingEnabled;
            this.classInfo = classInfo; // to be used in generateEvent()

            String[] listeners = oq.b(classInfo);
            if (listeners.length > 0) {
                for (String listener : listeners) {
                    /* Found functionally same instructions in vanilla Sketchware, keep it this way */
                    if (!this.listeners.contains(listener)) {
                        this.listeners.add(new ComponentEvents(listener));
                    }
                }

                switch (classInfo.a()) {
                    case "FirebaseDB":
                    case "FirebaseStorage":
                    case "FirebaseAuth":
                    case "Gyroscope":
                    case "InterstitialAd":
                    case "RequestNetwork":
                    case "BluetoothConnect":
                        for (ComponentEvents value : this.listeners) {
                            value.b = true;
                        }
                        break;

                    default:
                }
            }
        }

        private String generateEvent() {
            StringBuilder sb = new StringBuilder(4096);
            boolean webViewClientAdded = false; // Track if WebViewClient is added
            
            for (ComponentEvents value : listeners) {
                String event = value.generateEvent(id);
                
                // Ensure WebViewClient is always set
                if (classInfo.a().equals("WebView") && !webViewClientAdded) {
                    sb.append(id).append(".setWebViewClient(new WebViewClient());").append(Jx.EOL);
                    webViewClientAdded = true; // Mark it as added
                }
        
                if (sb.length() > 0 && !event.isEmpty()) {
                    sb.append(Jx.EOL);
                    sb.append(Jx.EOL);
                }
                sb.append(event);
            }
            
            // Remove the first WebViewClient if another setWebViewClient exists
            String output = sb.toString();
            if (classInfo.a().equals("WebView") && output.indexOf(".setWebViewClient(") != output.lastIndexOf(".setWebViewClient(")) {
                int firstIndex = output.indexOf(id + ".setWebViewClient(new WebViewClient());");
                if (firstIndex != -1) {
                    output = output.substring(0, firstIndex) + output.substring(firstIndex + (id + ".setWebViewClient(new WebViewClient());").length());
                }
            }

            return output.trim();
        }

        private void addEvent(String targetId, String eventName, String eventLogic) {
            for (ComponentEvents d : listeners) {
                for (ActivityEvent a : d.c) {
                    if (a.name.equals(eventName)) {
                        a.setLogic(eventLogic);
                        a.setTargetId(
                                targetId
                        );
                        d.b = true;
                    }
                }

                if (d.b) {
                    this.hx.imports.addAll(mq.d(d.a));
                }
            }
        }

    }

    private static class ComponentEvents {

        private final String a;
        private final ArrayList<ActivityEvent> c;
        /**
         * Probably "if associated to a Component"/"got its code added"
         */
        private boolean b = false;

        private ComponentEvents(String str) {
            a = str;
            c = new ArrayList<>();
            for (String eventName : oq.b(str)) {
                c.add(new ActivityEvent(eventName));
            }
        }

        private String generateEvent(String str) {
            if (!b) {
                return "";
            }
            StringBuilder sb = new StringBuilder(4096);
            for (ActivityEvent value : c) {
                String code = value.getCode();
                if (sb.length() > 0 && !code.isEmpty()) {
                    sb.append(Jx.EOL);
                    sb.append(Jx.EOL);
                }
                sb.append(code);
            }
            return Lx.getListenerCode(a, str, sb.toString());
        }
    }
}
