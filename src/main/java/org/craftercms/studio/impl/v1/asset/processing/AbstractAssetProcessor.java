/*
 * Copyright (C) 2007-2018 Crafter Software Corporation. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.studio.impl.v1.asset.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.studio.api.v1.asset.Asset;
import org.craftercms.studio.api.v1.asset.processing.AssetProcessor;
import org.craftercms.studio.api.v1.asset.processing.ProcessorConfiguration;
import org.craftercms.studio.api.v1.exception.AssetProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for {@link AssetProcessor}s.
 *
 * @author avasquez
 */
public abstract class AbstractAssetProcessor implements AssetProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAssetProcessor.class);

    @Override
    public Asset processAsset(ProcessorConfiguration config, Matcher inputPathMatcher, Asset input) throws AssetProcessingException {
        try {
            String outputRepoPath = getOutputRepoPath(config, inputPathMatcher);
            if (StringUtils.isEmpty(outputRepoPath)) {
                // No output repo path means write to the same input. So move the original file to a tmp location
                // and make the output file be the input file
                Path inputFile = moveToTmpFile(input.getRepoPath(), input.getFile());
                Path outputFile = input.getFile();
                Asset output = new Asset(input.getRepoPath(), outputFile);

                logger.info("Asset processing: Type = {}, Input = {}, Output = {}", config.getType(), input, output);

                try {
                    doProcessAsset(inputFile, outputFile, config.getParams());
                } finally {
                    Files.delete(inputFile);
                }

                return output;
            } else {
                Path inputFile = input.getFile();
                Path outputFile = createTmpFile(outputRepoPath);
                Asset output = new Asset(outputRepoPath, outputFile);

                logger.info("Asset processing: Type = {}, Input = {}, Output = {}", config.getType(), input, output);

                doProcessAsset(inputFile, outputFile, config.getParams());

                return output;
            }
        } catch (Exception e) {
            throw new AssetProcessingException("Error while executing asset processor of type '" + config.getType() + "'", e);
        }
    }

    private Path moveToTmpFile(String repoPath, Path file) throws IOException {
        Path tmpFile = createTmpFile(repoPath);

        return Files.move(file, tmpFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    private Path createTmpFile(String repoPath) throws IOException {
        return Files.createTempFile(FilenameUtils.getBaseName(repoPath), "." + FilenameUtils.getExtension(repoPath));
    }

    protected String getOutputRepoPath(ProcessorConfiguration config, Matcher inputPathMatcher) {
        if (StringUtils.isNotEmpty(config.getOutputPathFormat())) {
            int groupCount = inputPathMatcher.groupCount();
            String outputPath = config.getOutputPathFormat();

            for (int i = 1; i <= groupCount; i++) {
                outputPath = outputPath.replace("$" + i, inputPathMatcher.group(i));
            }

            return outputPath;
        } else {
            return null;
        }
    }

    protected abstract void doProcessAsset(Path inputFile, Path outputFile, Map<String, String> params) throws Exception;

}
