package de.scholzi100.incubator.resolver.modulefinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.stream.Stream;

public class MavenModuleReader implements ModuleReader {

    private final Logger LOGGER = LoggerFactory.getLogger(MavenModuleReader.class);


    @Override
    public Optional<URI> find(String name) throws IOException {
        LOGGER.trace("find {}", name);
        return Optional.empty();
    }

    @Override
    public Stream<String> list() throws IOException {
        LOGGER.trace("list");
        return null;
    }

    @Override
    public void close() throws IOException {
        LOGGER.trace("close");
    }

    //optional


    @Override
    public Optional<InputStream> open(String name) throws IOException {
        LOGGER.trace("open {}", name);
        return ModuleReader.super.open(name);
    }

    @Override
    public Optional<ByteBuffer> read(String name) throws IOException {
        LOGGER.trace("read {}", name);
        return ModuleReader.super.read(name);
    }

    @Override
    public void release(ByteBuffer bb) {
        LOGGER.trace("release {}", bb);
        ModuleReader.super.release(bb);
    }
}
