package net.sf.okapi.tm.pensieve.common;

/**
 * User: Christian Hargraves
 * Date: Aug 19, 2009
 * Time: 6:53:34 AM
 */
public class TranslationUnit {
    private TranslationUnitVariant source;
    private TranslationUnitVariant target;
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

    public TranslationUnit(TranslationUnitVariant source, TranslationUnitVariant target) {
        this();
        this.source = source;
        this.target = target;
    }

    public TranslationUnitVariant getSource() {
        return source;
    }

    public TranslationUnitVariant getTarget() {
        return target;
    }

    public void setSource(TranslationUnitVariant source) {
        this.source = source;
    }

    public void setTarget(TranslationUnitVariant target) {
        this.target = target;
    }

    public boolean isSourceEmpty() {
        return isFragmentEmpty(source);
    }

    public boolean isTargetEmpty() {
        return isFragmentEmpty(target);
    }

    private static boolean isFragmentEmpty(TranslationUnitVariant frag){
        return frag == null || frag.getContent().isEmpty();
    }
}
