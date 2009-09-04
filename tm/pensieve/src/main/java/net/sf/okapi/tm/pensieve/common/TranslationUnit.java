package net.sf.okapi.tm.pensieve.common;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:53:34 AM
 */
public class TranslationUnit {
    private TranslationUnitValue source;
    private TranslationUnitValue target;
    private MetaData metadata;

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }


    public TranslationUnit(){
        metadata = new MetaData();
    }

    public TranslationUnit(TranslationUnitValue source, TranslationUnitValue target) {
        this();
        this.source = source;
        this.target = target;
    }

    public TranslationUnitValue getSource() {
        return source;
    }

    public TranslationUnitValue getTarget() {
        return target;
    }

    public void setSource(TranslationUnitValue source) {
        this.source = source;
    }

    public void setTarget(TranslationUnitValue target) {
        this.target = target;
    }

    public boolean isSourceEmpty() {
        return isFragmentEmpty(source);
    }

    public boolean isTargetEmpty() {
        return isFragmentEmpty(target);
    }

    private static boolean isFragmentEmpty(TranslationUnitValue frag){
        return frag == null || frag.getContent().isEmpty();
    }
}
