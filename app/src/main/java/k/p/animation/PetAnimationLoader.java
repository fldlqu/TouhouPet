package k.p.animation;

import java.io.FileInputStream;
import k.p.exceptions.LoadXMLFailException;
import k.p.services.AnimationService;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class PetAnimationLoader {
    private String filePath;

    public PetAnimationLoader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reconstructed faithfully from the smali (the original jadx output of this
     * method was incorrect). NOTE: the original binary calls parser.next() twice
     * per loop iteration (a second call at the loop bottom whose result is
     * discarded, confirmed by smali .line 31 / .line 64 markers). This quirk is
     * preserved for behavioral fidelity.
     */
    public void load() throws LoadXMLFailException {
        String tagName = null;
        PetAnimation animation = null;
        PetAnimationInfo info = null;
        XmlPullParser parser;
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new FileInputStream(this.filePath), "UTF-8");
        } catch (Exception e) {
            throw new LoadXMLFailException(0, "");
        }
        int eventType;
        while (true) {
            try {
                eventType = parser.next();
            } catch (Exception e) {
                throw new LoadXMLFailException(parser.getLineNumber(), "");
            }
            if (eventType == XmlPullParser.END_DOCUMENT) {
                return;
            }
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    try {
                        tagName = parser.getName();
                        if ("animation".equals(tagName)) {
                            animation = new PetAnimation();
                            try {
                                if (parser.getAttributeValue(null, "loop") == null) {
                                    animation.setLoop(0);
                                } else {
                                    animation.setLoop(Integer.parseInt(parser.getAttributeValue(null, "loop")));
                                }
                            } catch (Exception e) {
                                throw new LoadXMLFailException(parser.getLineNumber(), "");
                            }
                            animation.setName(parser.getAttributeValue(null, "name"));
                            animation.setType(parser.getAttributeValue(null, "type").split("\\|"));
                        } else if ("animationinfo".equals(tagName)) {
                            try {
                                info = new PetAnimationInfo();
                                info.setPicPath(parser.getAttributeValue(null, "path"));
                                info.setDelay(Integer.parseInt(parser.getAttributeValue(null, "delay")));
                                animation.addActionInfo(info);
                            } catch (Exception e) {
                                throw new LoadXMLFailException(parser.getLineNumber(), "");
                            }
                        } else if ("settings".equals(tagName)) {
                            AnimationService.petWidth = Integer.parseInt(parser.getAttributeValue(null, "width"));
                            AnimationService.petHeight = Integer.parseInt(parser.getAttributeValue(null, "height"));
                        }
                    } catch (Exception e) {
                        throw new LoadXMLFailException(parser.getLineNumber(), "");
                    }
                    break;
                case XmlPullParser.END_TAG:
                    try {
                        tagName = parser.getName();
                        if ("animation".equals(tagName)) {
                            AnimationService.registerAnimation(animation);
                            animation = null;
                        }
                    } catch (Exception e) {
                        throw new LoadXMLFailException(parser.getLineNumber(), "");
                    }
                    break;
                default:
                    break;
            }
            try {
                parser.next();
            } catch (Exception e) {
                throw new LoadXMLFailException(parser.getLineNumber(), "");
            }
        }
    }
}
