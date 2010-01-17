package com.namebound.yeti.tmp;

/**
 *
 * @author Laurian Gridinoc
 */
class Representation {

    private Resource resource;

    public Resource resource() {
        return resource;
    }

    public boolean representationOf(Resource resource) {
        return this.resource.equals(resource);
    }
}
