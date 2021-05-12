
package de.scholzi100.incubator;

import java.util.*;

import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class ScopeDependencyFilter implements DependencyFilter {
    private final Set<String> included = new HashSet();
    private final Set<String> excluded = new HashSet();

    public ScopeDependencyFilter(Collection<String> included, Collection<String> excluded) {
        if (included != null) {
            this.included.addAll(included);
        }

        if (excluded != null) {
            this.excluded.addAll(excluded);
        }

    }

    public ScopeDependencyFilter(String... excluded) {
        if (excluded != null) {
            this.excluded.addAll(Arrays.asList(excluded));
        }

    }

    public boolean accept(DependencyNode node, List<DependencyNode> parents) {
        Dependency dependency = node.getDependency();
        if (dependency == null) {
            return true;
        } else {
            String scope = node.getDependency().getScope();
            return (this.included.isEmpty() || this.included.contains(scope)) && (this.excluded.isEmpty() || !this.excluded.contains(scope));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopeDependencyFilter)) return false;
        ScopeDependencyFilter that = (ScopeDependencyFilter) o;
        return Objects.equals(included, that.included) && Objects.equals(excluded, that.excluded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(included, excluded);
    }
}
